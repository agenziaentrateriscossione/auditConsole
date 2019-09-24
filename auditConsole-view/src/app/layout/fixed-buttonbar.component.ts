import { Component, HostListener } from '@angular/core';

@Component({
  template: ''
 })
export class FixedButtonBarComponent {

  fixedButtonBar = false;

  /**
   * Scroll all'inizio della pagina
   */
  scrollToTop() {
    window.scrollTo(0, 0);
  }

  /**
   * Listener su scrollbar della finestra del browser
   */
  @HostListener('window:scroll', ['$event'])
  onscroll($event) {
    if (window.pageYOffset >= 60) {
      this.fixedButtonBar = true;
    } else {
      this.fixedButtonBar = false;
    }
    // console.log('onScroll... fixedButtonBar = ' + this.fixedButtonBar);
  }

}
