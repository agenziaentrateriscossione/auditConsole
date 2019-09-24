import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbDateParserFormatter, NgbDatepickerI18n, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FieldErrorViewer, LoadingSpinner } from '@tredi/widgets-tredi';
import { BsModalError, NgbDateParserFormatterBrowser, NgbDatepickerI18nBrowser } from '@tredi/widgets-bs';
import { FormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { DateTimeValidator } from '../../validators/date-time.validator';
import { NgSelectModule } from '@ng-select/ng-select';
import { TranslateModule } from '@ngx-translate/core';
import { NgModule } from '@angular/core';
import { FiltersComponent } from './filters/filters.component';
import { ListComponent } from './list/list.component';
import { ViewComponent } from './view/view.component';
import { CommonModule } from '@angular/common';
import { AuditRecordComponent } from './audit-record.component';
import { RouterModule, Routes } from '@angular/router';
import { AuditHttpInterceptor } from '../../utils/http-interceptor';
import { AuditRecordService } from './audit-record.service';
import { CommonPipesModule } from '../../pipes/common-pipes.module';
import { AuditRecordRoutingModule } from './audit-record.routing.module';
import { BsProgressBar } from '@tredi/widgets-bs';

const routes: Routes = [
  { path: '', component: AuditRecordComponent }
];

@NgModule({
  declarations: [
    ListComponent,
    FiltersComponent,
    ViewComponent,
    DateTimeValidator,
    AuditRecordComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    FontAwesomeModule,
    HttpClientModule,
    NgSelectModule,
    NgbModule,
    LoadingSpinner,
    BsModalError,
    CommonPipesModule,
    FieldErrorViewer,
    AuditRecordRoutingModule,
    TranslateModule,
    RouterModule.forChild(routes),
    BsProgressBar
  ],
  providers: [
    AuditRecordService,
    // Personalizzazioni @tredi su datepicker di ng-boostrap
    { provide: NgbDateParserFormatter, useClass: NgbDateParserFormatterBrowser },
    { provide: NgbDatepickerI18n, useClass: NgbDatepickerI18nBrowser },
    // Override HTTP
    { provide: HTTP_INTERCEPTORS, useClass: AuditHttpInterceptor, multi: true }
  ]
})
export class AuditRecordModule { }
