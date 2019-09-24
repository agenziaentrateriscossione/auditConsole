import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs/internal/BehaviorSubject';
import Page from '../../models/page';
import SearchForm from '../../models/search-form';
import DateTimeUtils from '../../utils/date-time-utils';
import GenericUtils from '../../utils/generic-utils';
import { map } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { AppSettings } from '../../settings';
import { PositionInfo } from '../../models/audit-record';
import { Observable } from 'rxjs/internal/Observable';
import { NgbTime } from '@ng-bootstrap/ng-bootstrap/timepicker/ngb-time';

@Injectable()
export class AuditRecordService {

  private pageMsg = new BehaviorSubject(new Page);
  currentPage = this.pageMsg.asObservable();


  /**
   * Restituisce l'url per la richiesta dell'elenco di risultati,
   * filtrati in base ai parametri passati.
   * @param {number} page - pagina richiesta
   * @param {number} size - numero di risultati per pagina
   * @param {SearchForm} searchForm - form dei filtri
   * @returns {string}
   */
  private static queryRqUrl(
    page: number,
    size: number,
    searchForm: SearchForm): string {
    let url = AppSettings.API_ENDPOINT + `/audit/search?page=${page}&size=${size}`;
    url = this.appendSearchFilters(url, searchForm);
    return url;
  }

  /**
   * Appende all'URL passato tutti i parametri che identificano il filtro di ricerca
   * @param url - url al quale appendere i parametri di ricerca
   * @param searchForm - filtri di ricerca da applicare
   */
  private static appendSearchFilters(url: string, searchForm: SearchForm): string {
    if (url && searchForm) {
      let amp = false;
      if (url.indexOf('?') !== -1) {
        amp = true;
      }
      if (searchForm.archivio) {
        if (amp) {
          url += `&archivio=${searchForm.archivio}`;
        } else {
          url += `?archivio=${searchForm.archivio}`;
          amp = true;
        }
      }
      if (searchForm.tipoRecord) {
        if (amp) {
          url += `&tipoRecord=${searchForm.tipoRecord}`;
        } else {
          url += `?tipoRecord=${searchForm.tipoRecord}`;
          amp = true;
        }
      }
      if (searchForm.idRecord) {
        if (amp) {
          url += `&idRecord=${searchForm.idRecord}`;
        } else {
          url += `?idRecord=${searchForm.idRecord}`;
          amp = true;
        }
      }
      if (searchForm.tipoAzione) {
        for (const action of searchForm.tipoAzione) {
          if (amp) {
            url += `&tipoAzione=${action}`;
          } else {
            url += `?tipoAzione=${action}`;
            amp = true;
          }
        }
      }
      if (searchForm.username) {
        if (amp) {
          url += `&username=${searchForm.username}`;
        } else {
          url += `?username=${searchForm.username}`;
          amp = true;
        }
      }
      if (searchForm.codUser) {
        if (amp) {
          url += `&codUser=${searchForm.codUser}`;
        } else {
          url += `?codUser=${searchForm.codUser}`;
          amp = true;
        }
      }
      if (searchForm.dateFrom) {
        if (!searchForm.timeFrom) {
          searchForm.timeFrom = new NgbTime(0, 0, 0);
        }
        if (amp) {
          url += `&from=${encodeURIComponent(DateTimeUtils.formatDate(searchForm.dateFrom, searchForm.timeFrom))}`;
        } else {
          url += `?from=${encodeURIComponent(DateTimeUtils.formatDate(searchForm.dateFrom, searchForm.timeFrom))}`;
          amp = true;
        }
      }
      if (searchForm.dateTo) {
        if (!searchForm.timeTo) {
          searchForm.timeTo = new NgbTime(23, 59, 59);
        }
        if (amp) {
          url += `&to=${encodeURIComponent(DateTimeUtils.formatDate(searchForm.dateTo, searchForm.timeTo))}`;
        } else {
          url += `?to=${encodeURIComponent(DateTimeUtils.formatDate(searchForm.dateTo, searchForm.timeTo))}`;
          amp = true;
        }
      }
    }
    return url;
  }

  constructor(private http: HttpClient) {
    this.init();
  }

  /**
   * Inizializzazione (query senza parametri di ricerca)
   */
  init(): void {
    this.executeQueryRq(0, AppSettings.ELEMENT_PER_PAGE, new SearchForm, true, false);
  }

  /**
   * Avvisa tutti i componenti che utilizzano il service (con subscribe) che la pagina ha subito modifiche
   * @param {Page} page
   */
  changePage(page: Page): void {
    this.pageMsg.next(page);
    this.pageMsg.value.newQuery = false;
  }

  /**
   * Aggiorna la pagina corrente in base ai filtri passati come parametro
   * @param {SearchForm} filters
   */
  changeFilters(filters: SearchForm): void {
    this.executeQueryRq(0, AppSettings.ELEMENT_PER_PAGE, filters, true, false);
  }

  /**
   * Aggiorna la posizione della scrollbar per la pagina corrente
   * @param {number} pos
   */
  changeScrollbarPosition(pos: number): void {
    const updatedPage = this.pageMsg.value;
    updatedPage.scrollbarPosition = pos;
    this.changePage(updatedPage);
  }

  /**
   * Richiede l'observable relativo alla richiesta di una nuova pagina al server.
   * @param {number} page - numero della pagina
   * @param {number} size - numero di risultati per pagina
   * @param {SearchForm} searchForm - filtri di ricerca
   * @param {boolean} override - flag che specifica se sovrascrivere la precedente ricerca
   * @param {boolean} background - flache che specifica se eseguire la richiesta in background (no loading)
   * @return {Observable<Page>}
   */
  executeQueryObservable(page: number, size: number, searchForm: SearchForm, override: boolean, background: boolean): Observable<Page> {
    return this.http
      .get<Page>(AuditRecordService.queryRqUrl(page, size, searchForm), { headers: { background: String(background) }})
      .pipe(map(data => data));
  }

  /**
   * Dato il filtro di ricerca dei record ritorna l'URL da utilizzare per l'esportazione
   * @param searchForm - filtri di ricerca
   */
  getExportRecordsURL(searchForm: SearchForm): string {
    return AuditRecordService.appendSearchFilters(AppSettings.API_ENDPOINT + `/audit/export`, searchForm);
  }

  /**
   * Esegue la richiesta di una determinata pagina al service e aggiorna adeguatamente i campi controllati dal service.
   * @param {number} page - numero della pagina
   * @param {number} size - numero di risultati per pagina
   * @param {SearchForm} searchForm - filtri di ricerca
   * @param {boolean} override - flag che specifica se sovrascrivere la precedente ricerca
   * @param {boolean} background - flache che specifica se eseguire la richiesta in background (no loading)
   */
  executeQueryRq(page: number, size: number, searchForm: SearchForm, override: boolean, background: boolean): void {
    this.executeQueryObservable(page, size, searchForm, override, background)
      .subscribe(results => {
        if (results) {
          const newPage: Page = Page.deserialize(results);
          newPage.filters = searchForm;
          if (override) {
            newPage.newQuery = true;
            this.changePage(newPage);
          } else {
            this.addPage(newPage);
          }
        }
      });
  }

  /**
   * Aggiunge una pagina alla lista di risultati.
   * @param {Page} newPageList - pagina da aggiungere
   */
  addPage(newPageList: Page): void {
    const updatedPage = this.pageMsg.value;
    updatedPage.number = newPageList.number;
    updatedPage.first = newPageList.first;
    updatedPage.last = newPageList.last;
    updatedPage.content = GenericUtils.uniqueMerge(updatedPage.content, newPageList.content, 'id');
    updatedPage.newQuery = newPageList.newQuery;
    this.changePage(updatedPage);
  }

  /**
   * Controlla se l'audit corrente ha record precedenti e successivi nell'array di risultati nel quale è contenuto
   * @param {string} id - id dell'audit record da controllare
   * @returns {PositionInfo}
   */
  checkAuditRecordPosition(id: string): PositionInfo {
    const allRecords = this.pageMsg.value.content;
    let hasPrev = false;
    let hasNext = false;
    if (allRecords) {
      // ha precedenti se non è il primo
      hasPrev = allRecords[0].id !== id;
      // ha successivi se non è l'ultima pagina o non è l'ultimo dell'array degli AuditRecord
      hasNext = !this.pageMsg.value.last || allRecords[allRecords.length - 1].id !== id;
    }
    return new PositionInfo(hasPrev, hasNext);
  }

  /**
   * Trova nell'array dei risultati l'audit record successivo a quello passato come argomento ed effettua un
   * redirect alla sua view
   * @param {string} id
   * @return {string}
   */
  getPrevAuditRecordId(id: string): string {
    const allRecords = this.pageMsg.value.content;
    if (id && allRecords) {
      for (const pos in allRecords) {
        if (allRecords[+pos].id === id) {
          return allRecords[+pos - 1].id;
        }
      }
    }
  }

  /**
   * Trova nell'array dei risultati l'audit record precedente a quello passato come argomento
   * @param {string} id
   * @return {string}
   */
  getNextAuditRecordId(id: string): Promise<string> {
    return new Promise((resolve, reject) => {
        const allRecords = this.pageMsg.value.content;
        if (id && allRecords) {
          for (const pos in allRecords) {
            if (allRecords[+pos].id === id) {
              // necessario caricare prossima pagina
              if (+pos + 2 > allRecords.length) {
                this.executeQueryObservable(this.pageMsg.value.number + 1,
                            AppSettings.ELEMENT_PER_PAGE,
                            this.pageMsg.value.filters,
                            false,
                            false)
                  .subscribe(results => {
                    if (results) {
                      const newPage: Page = Page.deserialize(results);
                      this.addPage(newPage);
                      resolve(newPage.content[0].id);
                    }
                  });
              } else { // risultato disponibile
                resolve(allRecords[+pos + 1].id);
              }
            }
          }
        }
    });
  }
}
