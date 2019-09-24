import { NgbDateStruct, NgbTimeStruct } from '@ng-bootstrap/ng-bootstrap';

export default class DateTimeUtils {

  /**
   * Ottiene il giorno corrente e lo converte in oggetto compatibile con l'interfaccia NgbDateStruct
   * @returns {NgbDateStruct}
   */
  static getToday(): NgbDateStruct {
    const today = new Date();
    const day = today.getDate();
    const month = today.getMonth() + 1;
    const year = today.getFullYear();
    return {year, month, day};
  }

  /**
   * Controlla il primo parametro in formato NgbDateStruct corrisponde ad una data successiva a quella del
   * secondo parametro
   * @param {NgbDateStruct} date1 - data presa in considerazione
   * @param {NgbDateStruct} date2 - data da confrontare
   * @returns {boolean}
   */
  static isNgbDateAfter(date1: NgbDateStruct, date2: NgbDateStruct): boolean {
    if (!(date1 && date2)) {
      throw new Error('Arguments are not valid!');
    } else {
      if (date1.year > date2.year) {
        return true;
      }
      if (date1.year < date2.year) {
        return false;
      }
      if (date1.month > date2.month) {
        return true;
      }
      if (date1.month < date2.month) {
        return false;
      }
      return date1.day > date1.day;
    }
  }

  /**
   * Controlla se i due parametri in formato NgbDateStruct sono la stessa data
   * @param {NgbDateStruct} date1
   * @param {NgbDateStruct} date2
   * @returns {boolean}
   */
  static isNgbDateEqual(date1: NgbDateStruct, date2: NgbDateStruct): boolean {
    return date1.year === date2.year && date1.month === date2.month && date1.day === date2.day;
  }

  /**
   * Controlla il primo parametro in formato NgbTimeStruct corrisponde ad un orario successivo a quello del
   * secondo parametro
   * @param {NgbTimeStruct} time1
   * @param {NgbTimeStruct} time2
   * @returns {boolean}
   */
  static isNgbTimeAfter(time1: NgbTimeStruct, time2: NgbTimeStruct): boolean {
    if (!(time1 && time2)) {
      throw new Error('Arguments are not valid!');
    } else {
      if (time1.hour > time2.hour) {
        return true;
      }
      if (time1.hour < time2.hour) {
        return false;
      }
      return time1.minute > time2.minute;
    }
  }

  /**
   * Formatta le data e orari passati dai component ng-bootstrap in formato ISO: "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
   * @param {NgbDateStruct} date
   * @param {NgbTimeStruct} time
   * @returns {Date}
   */
  static formatDate(date: NgbDateStruct, time: NgbTimeStruct): string {
    const result = new Date(date.year, date.month - 1, date.day);
    if (time) {
      result.setHours(time.hour, time.minute, 0, 0);
    }
    // return result.toISOString();

   return result.getUTCFullYear()
            + '-' + DateTimeUtils.padDigits((result.getUTCMonth() + 1), 2)
            + '-' + DateTimeUtils.padDigits(result.getUTCDate(), 2)
            + 'T'
            + DateTimeUtils.padDigits(result.getUTCHours(), 2)
            + ':' + DateTimeUtils.padDigits(result.getUTCMinutes(), 2)
            + ':' + DateTimeUtils.padDigits(result.getUTCSeconds(), 2)
            + '.' + DateTimeUtils.padDigits(result.getUTCMilliseconds(), 3)
            + '+0000';
  }

  private static padDigits(number: number | string, digits: number): string {
    return Array(Math.max(digits - String(number).length + 1, 0)).join('0') + number;
  }

}
