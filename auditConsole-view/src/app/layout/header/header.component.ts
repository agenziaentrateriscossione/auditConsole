import { Component } from '@angular/core';
import RemoteUser from '../../models/remote-user';
import RemoteUserUtils from '../../utils/remote-user.utils';
import { AppSettings } from '../../settings';
import { faCaretDown, faUser } from '@fortawesome/free-solid-svg-icons';
import { faCircle } from '@fortawesome/free-regular-svg-icons/faCircle';
import { TranslateService } from '@ngx-translate/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap/modal/modal-ref';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {

  remoteUser: RemoteUser;
  appName: string = AppSettings.APP_NAME;
  isNavbarCollapsed = true;

  // fontawesome
  faCircle = faCircle;
  faUser = faUser;
  faCaretDown = faCaretDown;

  // lang modal
  enFlag = 'assets/images/en.png';
  itFlag = 'assets/images/it.png';
  private modalRef: NgbModalRef;


  constructor(private translateService: TranslateService, private modalService: NgbModal) {
    this.remoteUser = RemoteUserUtils.getCurrentUser();
  }

  /**
   * Cambia la lingua correntemente utilizzata
   * @param {string} lang
   */
  switchLang(lang: string) {
    if (lang) {
      this.translateService.use(lang);
      sessionStorage.setItem("lang", lang);
      this.modalRef.close();
    }
  }

  /**
   * Apre la modale per il cambio della lingua
   * @param content
   */
  openLangModal(content) {
    this.modalRef = this.modalService.open(content, { centered: true });
  }

}
