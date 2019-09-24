import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  constructor(private translateService: TranslateService) {
    const previouslyChosenLang = sessionStorage.getItem('lang');
    const browserLang = translateService.getBrowserLang();
    const defaultLang = 'it';
    const langToUse = previouslyChosenLang || browserLang || defaultLang;
    translateService.addLangs(['en', 'it']);
    translateService.setDefaultLang(defaultLang);
    translateService.use(langToUse.match(/en|it/) ? langToUse : defaultLang);
  }

}
