import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ViewComponent } from './view/view.component';
import { AuthGuard } from '../../services/auth.guard';

const routes: Routes = [
  { path: 'record/:id/view', component: ViewComponent, canActivate: [AuthGuard]}
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [RouterModule]
})
export class AuditRecordRoutingModule { }
