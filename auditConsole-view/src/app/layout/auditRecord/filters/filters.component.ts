import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { faCalendarAlt } from '@fortawesome/free-regular-svg-icons';
import { faFilter, faSyncAlt } from '@fortawesome/free-solid-svg-icons';
import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import SearchForm from '../../../models/search-form';
import DateTimeUtils from '../../../utils/date-time-utils';
import { AuditRecordService } from '../audit-record.service';
import { Subscription } from 'rxjs/internal/Subscription';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { AppSettings } from '../../../settings';

@Component({
  selector: 'app-filters',
  templateUrl: './filters.component.html',
  styleUrls: ['./filters.component.css'],
  animations: [
    trigger('toggleFilterMenu', [
      state('visible', style({
        position: 'absolute',
        top: '0',
        left: '0',
        right: '0',
        zIndex: 100,
      })),
      state('invisible', style({
        position: 'absolute',
        top: '-1000px',
        left: '0',
        right: '0',
        zIndex: 100
      })),
      transition('invisible <=> visible', animate('750ms ease-out'))
    ]),
    trigger('rotateFilterIcon', [
      state('visible', style({
        transform: 'rotate(180deg)'
      })),
      state('invisible', style({
        transform: 'rotate(0deg)'
      })),
      transition('invisible <=> visible', animate('300ms ease-out'))
    ])
  ]
})
export class FiltersComponent implements OnInit, OnDestroy {

  searchForm: SearchForm = new SearchForm();
  today: NgbDateStruct;
  subscription: Subscription;
  visibilityState = 'invisible';

  // fontawesome
  faCalendarAlt = faCalendarAlt;
  faFilter = faFilter;
  faSyncAlt = faSyncAlt;

  /**
   * Url per la richiesta dell'elenco degli archivi
   * @type {string}
   */
  private readonly archiveRqUrl: string = AppSettings.API_ENDPOINT + '/audit/dbs';

  /**
   * Restituisce l'url per la richiesta dell'elenco di tutti
   * i tipi di record, filtrati in base al db.
   * @param {string} db - tipo di archivio
   * @returns {string}
   */
  private static recordTypesRqUrl(db: string): string {
    return AppSettings.API_ENDPOINT + `/audit/${db}/recordTypes`;
  }

  /**
   * Restituisce l'url per la richiesta dell'elenco di tutti
   * i tipi di azione, filtrati in base al db.
   * @param {string} db - tipo di archivio
   * @returns {string}
   */
  private static actionsRqUrl(db: string): string {
    return AppSettings.API_ENDPOINT + `/audit/${db}/actions`;
  }

  constructor(private http: HttpClient, private mainService: AuditRecordService) {}

  ngOnInit() {
    this.subscription = this.mainService.currentPage.subscribe(page => {
      this.searchForm = page.filters;
      if (!this.searchForm.archives) {
        this.getArchives();
      }
    });
    this.getTodayBound();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  /**
   * Richiesta REST alla lista degli archivi
   */
  getArchives(): void {
    this.http
      .get<string[]>(this.archiveRqUrl)
      .pipe(map(data => data))
      .subscribe(dbs => {
        this.searchForm.archives = dbs;
      });
  }

  /**
   * Selezione di un archivio, scatena la query REST per i tipoAzione
   * relativi all'archivio e sblocca la relativa select
   */
  onArchiveSelected(db: string): void {
    if (db) {
      this.searchForm.recordTypes = [];
      this.searchForm.actions = [];

      this.http
        .get<string[]>(FiltersComponent.recordTypesRqUrl(db))
        .pipe(map(data => data))
        .subscribe(recordTypes => {
          this.searchForm.recordTypes = recordTypes;
        });
      this.http
        .get<string[]>(FiltersComponent.actionsRqUrl(db))
        .pipe(map(data => data))
        .subscribe(actions => {
          this.searchForm.actions = actions;
        });
    } else {
      this.searchForm.recordTypes = [];
      this.searchForm.actions = [];
      this.cleanSearchFormArchiveFields();
    }
  }

  /**
   * Forza il refresh della select relativa alle tipologie di record
   */
  refreshTipiRecord(): void {
    if (this.searchForm.archivio) {
      this.searchForm.recordTypes = [];

      this.http
        .get<string[]>(FiltersComponent.recordTypesRqUrl(this.searchForm.archivio) + '?reload=true')
        .pipe(map(data => data))
        .subscribe(recordTypes => {
          this.searchForm.recordTypes = recordTypes;
        });
    }
  }

  /**
   * * Forza il refresh della select relativa alle tipologie di azione
   */
  refreshTipiAzione(): void {
    if (this.searchForm.archivio) {
      this.searchForm.actions = [];

      this.http
        .get<string[]>(FiltersComponent.actionsRqUrl(this.searchForm.archivio) + '?reload=true')
        .pipe(map(data => data))
        .subscribe(actions => {
          this.searchForm.actions = actions;
        });
    }
  }

  /**
   * Setta il limite di oggi delle data selezionabili dal datepicker
   * per evitare di poter selezionare inutile data future
   */
  getTodayBound(): void {
    this.today = DateTimeUtils.getToday();
  }

  /**
   * Aggiorna la data di fine in base all'ultimo inserimento della data di inizio
   * se necessario
   */
  updateDataTo(): void {
    const newDate = this.searchForm.dateFrom;
    const toDate = this.searchForm.dateTo;
    if (newDate && toDate && DateTimeUtils.isNgbDateAfter(newDate, toDate)) {
      this.searchForm.dateTo = newDate;
    }
  }

  /**
   * Aggiorna la data di inizio in base all'ultimo inserimento della data di fine
   * se necessario
   */
  updateDataFrom(): void {
    const newDate = this.searchForm.dateTo;
    const fromDate = this.searchForm.dateFrom;
    if (newDate && fromDate && DateTimeUtils.isNgbDateAfter(fromDate, newDate)) {
      this.searchForm.dateFrom = newDate;
    }
  }

  /**
   * Pulisce i campi relativi all'archivio quando questo viene deselezionato
   */
  cleanSearchFormArchiveFields(): void {
    this.searchForm.archivio = undefined;
    this.searchForm.tipoRecord = undefined;
    this.searchForm.tipoAzione = undefined;
  }

  /**
   * Pulisce tutti i campi del form
   */
  cleanSearchFormFields(): void {
    const archives = this.searchForm.archives;
    delete this.searchForm;
    this.searchForm = new SearchForm();
    this.searchForm.archives = archives;
  }

  /**
   * Gestore dell'evento submit del form di ricerca
   */
  onSubmit(): void {
    this.mainService.changeFilters(this.searchForm);
    this.toggleVisibility();
  }

  /**
   * Gestisce la visibilità del menù dei filtri
   */
  toggleVisibility() {
    if (document.documentElement.clientWidth < AppSettings.BOOTSTRAP_MINOR_MD_BREAKPOINT) {
      this.visibilityState = this.visibilityState === 'visible' ? 'invisible' : 'visible';
    }
  }
}
