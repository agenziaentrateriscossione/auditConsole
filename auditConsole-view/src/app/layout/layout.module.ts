import { NgModule } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { BsModalError } from '@tredi/widgets-bs';
import { LoadingSpinner } from '@tredi/widgets-tredi';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { AuditHttpInterceptor } from '../utils/http-interceptor';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { LayoutRoutingModule } from './layout-routing.module';
import { LayoutComponent } from './layout.component';
import { HeaderComponent } from './header/header.component';
import { FixedButtonBarComponent } from './fixed-buttonbar.component';

@NgModule({
  declarations: [
    LayoutComponent,
    HeaderComponent,
    FixedButtonBarComponent
  ],
  imports: [
    NgbModule,
    CommonModule,
    FontAwesomeModule,
    LoadingSpinner,
    BsModalError,
    LayoutRoutingModule,
    TranslateModule
  ],
  providers: [
    // Override HTTP
    { provide: HTTP_INTERCEPTORS, useClass: AuditHttpInterceptor, multi: true },
  ],
})
export class LayoutModule {}
