import { AppSettings } from '../../../settings';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { AuditRecord, PositionInfo } from '../../../models/audit-record';
import { faArrowLeft, faArrowRight, faList } from '@fortawesome/free-solid-svg-icons';
import { AuditRecordService } from '../audit-record.service';
import { Subscription } from 'rxjs/internal/Subscription';
import { FixedButtonBarComponent } from '../../fixed-buttonbar.component';

@Component({
  selector: 'app-view',
  templateUrl: './view.component.html',
  styleUrls: ['./view.component.css']
})
export class ViewComponent extends FixedButtonBarComponent implements OnInit, OnDestroy {

  record: AuditRecord = new AuditRecord();
  recordPosInfo = new PositionInfo();
  routeSubscription: Subscription;
  applicationUrl: string;

  hideIpAddress = true;

  // fontawesome
  faList = faList;
  faArrowLeft = faArrowLeft;
  faArrowRight = faArrowRight;

  private static recordRqUrl(id: string): string {
    return AppSettings.API_ENDPOINT + `/audit/${id}/view`;
  }

  constructor(
    private activatedRoute: ActivatedRoute,
    private http: HttpClient,
    private mainService: AuditRecordService,
    private router: Router) {
      super();
  }

  ngOnInit() {
    this.routeSubscription = this.activatedRoute.params.subscribe(params => {
      const id = params['id'];
      this.http
        .get<string[]>(ViewComponent.recordRqUrl(id))
        .pipe(map(data => data))
        .subscribe(jsonRecord => {
          if (jsonRecord) {
            this.record = AuditRecord.deserialize(jsonRecord);
            if (this.record) {
              this.recordPosInfo = this.mainService.checkAuditRecordPosition(id);
              this.applicationUrl = this.extractApplicationUrl();
            }
          }
        });
      }
    );
  }

  private initHideIpAddress(): void {
    const limits = JSON.parse(localStorage.getItem(AppSettings.APP_SETTINGS_LOCALSTORE_KEY));
    if (limits && limits[AppSettings.HIDE_IP_ADDRESS]) {
      this.hideIpAddress = limits[AppSettings.HIDE_IP_ADDRESS];
    }
  }

  ngOnDestroy() {
    this.routeSubscription.unsubscribe();
  }

  /**
   * Interroga il main service chiedendo il record precedente
   */
  prevAuditRecord(): void {
    const prevRecordId = this.mainService.getPrevAuditRecordId(this.record.id);
    this.router.navigate(['record', prevRecordId, 'view']);
  }

  /**
   * Interroga il main service chiedendo il record successivo, nel caso
   * in cui sia necessario carica la prossima pagina (per questo gestito con Promise)
   */
  nextAuditRecord(): void {
    this.mainService.getNextAuditRecordId(this.record.id)
    .then(id => {
      this.router.navigate(['record', id, 'view']);
    });
  }

  /**
   * Estrae dal localStorage la configurazione degli URL diretti all'applicazione, verifica se il record di audit contiene
   * le informazioni necessarie per la visualizzazione del relativo documento sull'applicazione auditata e ne costruisce
   * il link a partire da questi.
   */
  private extractApplicationUrl(): string {
    const appUrlMapping = JSON.parse(localStorage.getItem(AppSettings.APP_URL_MAPPER_LOCALSTORE_KEY));
    if (this.record && appUrlMapping && this.record.archivio && this.record.tipoRecord) {
      const urlMap: any = appUrlMapping.maps.find(val => {
        return val.archive === this.record.archivio && val.type === this.record.tipoRecord;
      });
      if (urlMap && urlMap.url) {
        return urlMap.url.replace(/\$\{\w+\}/g, word => {
          switch (word) {
            case '${id}':
              return this.record.idRecord;
            case '${archive}':
              return this.record.archivio;
            case '${type}':
              return this.record.tipoRecord;
            case '${username}':
              return this.record.user.actualUsername;
            case '${userId}':
              return this.record.user.actualCodUser;
            default:
              return word;
          }
        });
      }
    }
    return null;
  }
}
