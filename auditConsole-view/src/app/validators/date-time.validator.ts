import { Directive, forwardRef } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';
import DateTimeUtils from '../utils/date-time-utils';

@Directive({
  selector: '[dateTimeCheck][ngModel],[dateTimeCheck][formControl]',
  providers: [{ provide: NG_VALIDATORS, useExisting: forwardRef(() => DateTimeValidator), multi: true }]
})
export class DateTimeValidator implements Validator  {

  validate(control: AbstractControl): ValidationErrors {
    return DateTimeValidator.checkDatesAndTimes(control);
  }

  /**
   * Controlla che se le tutti i campi data e orario sono valorizzati, l'orario di fine non sia
   * precedente all'orario di inizio.
   * @param {AbstractControl} control
   * @returns {ValidationErrors}
   */
  private static checkDatesAndTimes(control: AbstractControl): ValidationErrors {
    const parentControl = control.parent;
    if (parentControl) {
      const dateFromControl = parentControl.get('dateFrom');
      const dateToControl = parentControl.get('dateTo');
      const timeFromControl = parentControl.get('timeFrom');
      const timeToControl = parentControl.get('timeTo');
      // non dovrebbe mai fallire
      if (dateFromControl && dateToControl && timeFromControl && timeToControl) {
        const dateFrom = dateFromControl.value;
        const dateTo = dateToControl.value;
        const timeFrom = timeFromControl.value;
        const timeTo = timeToControl.value;
        if (dateFrom && dateTo && timeFrom && timeTo &&
            DateTimeUtils.isNgbDateEqual(dateFrom, dateTo) &&
            DateTimeUtils.isNgbTimeAfter(timeFrom, timeTo)) {
          // errore
          const timeError = { 'ngbTime': { time_from: timeFrom, time_to: timeTo } };
          // gestione errore solo per gli orari
          if (control === timeFromControl) {
            timeToControl.setErrors(timeError);
            return timeError;
          }
          else if (control === timeToControl) {
            timeFromControl.setErrors(timeError);
            return timeError;
          }
          else {
            timeFromControl.setErrors(timeError);
            timeToControl.setErrors(timeError);
          }
        }
        else {
          timeFromControl.setErrors(null);
          timeToControl.setErrors(null);
        }
      }
    }
    return null;
  }
}



