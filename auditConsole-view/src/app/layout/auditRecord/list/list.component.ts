import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Page from '../../../models/page';
import { HttpClient } from '@angular/common/http';
import { AuditRecordService } from '../audit-record.service';
import { AppSettings } from '../../../settings';
import { Subscription } from 'rxjs/internal/Subscription';
import { faDownload } from '@fortawesome/free-solid-svg-icons';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css']
})
export class ListComponent implements AfterViewInit, OnInit, OnDestroy {

  page: Page = new Page();
  showLoading = false;
  readonly RECORD_SEARCH_RESULTS_ID = 'record-search-results';
  subscription: Subscription;

  exportActive = false;

  // fontawesome
  faDownload = faDownload;

  constructor(private http: HttpClient,
                    private router: Router,
                    private mainService: AuditRecordService,
                    private translate: TranslateService) { }

  ngOnInit(): void {
    this.subscription = this.mainService.currentPage.subscribe(page => {
      this.page = page;
      this.showLoading = false;
      if (this.page.newQuery) {
        this.restoreScrollbarPosition(0);
      }
    });
  }

  ngAfterViewInit(): void {
    this.restoreScrollbarPosition();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  /**
   * Chiusura del popup modale di progressbar su export di record
   */
  hideExport(): void {
    this.exportActive = false;
  }

  /**
   * Carica altre pagine di risultati quando lo scroll del div dei titoli si avvicina alla fine.
   */
  onScroll() {
    if (!this.page.last) {
      const scrollElement: HTMLElement = this.getTitlesElement();
      if (scrollElement) {
        const a = scrollElement.scrollTop;
        const b = scrollElement.scrollHeight - scrollElement.clientHeight;
        if ((b - a) < 10) {
          // in questo modo si evitano richieste parallele mentre l'utente muove la scrollbar
          if (!this.showLoading) {
            this.showLoading = true;
            this.mainService.executeQueryRq(this.page.number + 1, AppSettings.ELEMENT_PER_PAGE, this.page.filters, false, true);
          }
        }
      }
    }
  }

  /**
   * Ritorna l'elemento HTML che contiene la lista titoli (elemento con scrollbar).
   * @returns {HTMLElement}
   */
  getTitlesElement(): HTMLElement {
    return document.getElementById(this.RECORD_SEARCH_RESULTS_ID);
  }

  /**
   * Caricamento della pagina di visualizzazione di un record.
   * @param {string} id - Identificativo del record da caricare
   */
  loadRecord(id: string): void {
    // salvataggio della scrollbar position
    this.saveScrollbarPosition();
    this.router.navigate(['record', id, 'view']);
  }

  /**
   * Ritorna l'URL di esportazione CSV dei record risultanti dalla ricerca
   */
  getExportUrl(): string {
    return this.mainService.getExportRecordsURL(this.page.filters);
  }

  /**
   * Esportazione CSV dei record risultanti dalla ricerca
   */
  export(): void {
    if (this.page) {
      const limits = JSON.parse(localStorage.getItem(AppSettings.APP_SETTINGS_LOCALSTORE_KEY));

      let startExport = false;
      const exportMaxSize: number = this.getSizeLimit(limits, AppSettings.EXPORT_MAX_SIZE_KEY);
      const validationMaxSize: number = this.getSizeLimit(limits, AppSettings.EXPORT_VALIDATION_MAX_SIZE_KEY);

      if (exportMaxSize === 0 || exportMaxSize >= this.page.totalElements) {
        if (validationMaxSize === 0 || validationMaxSize >= this.page.totalElements) {
          startExport = confirm(
            this.translateMessage('list.Confermare l\'esportazione di X record?', {'tot': this.page.totalElements})
          );
        } else {
          // numero di record troppo elevato per la validazione, esportazione con validazione disabilitata ...
          startExport = confirm(
            this.translateMessage('list.Validazione non permessa: max = X. Confermare comunque l\'esportazione di Y record?',
              {'validationMax': validationMaxSize, 'tot': this.page.totalElements})
          );
        }
      } else {
        // numero di risultati eccessivo rispetto ai limiti dell'esportazione
        alert(this.translateMessage('list.Numero di record troppo elevato per permetterne l\'esportazione, max = X', {max: exportMaxSize}));
        startExport = false;
      }

      if (startExport) {
        // caricamento loadingbar
        this.exportActive = true;
      }
    }
  }

  /**
   * Data la mappa dei limiti applicativi, restituisce il limite definito in base alla chiave passata
   * @param limits
   * @param limitKey
   */
  private getSizeLimit(limits: any, limitKey: string): number {
    let value = 0;
    if (limits && limitKey && limits[limitKey]) {
      value = limits[limitKey];
    }
    return value;
  }

  /**
   * Traduzione di un messaggio
   * TODO Andrebbe magari spostato su una libreria specifica
   * @param message
   * @package values
   */
  private translateMessage(message: string, values?: any): string {
    this.translate.get(message, values).subscribe(value => {
      message = value;
    });
    return message;
  }

  /**
   * Salva la posizione della scrollbar della tabella dei titoli nel main service.
   */
  private saveScrollbarPosition(): void {
    const scrollElement: HTMLElement = this.getTitlesElement();
    this.mainService.changeScrollbarPosition(scrollElement.scrollTop);
  }

  /**
   * Ripristina la posizione della scrollbar della tabella dei titoli
   */
  private restoreScrollbarPosition(pos?: number): void {
    const scrollPosition = pos || this.page.scrollbarPosition || 0;
    const scrollElement: HTMLElement = this.getTitlesElement();
    scrollElement.scrollTop = scrollPosition;
  }

}
