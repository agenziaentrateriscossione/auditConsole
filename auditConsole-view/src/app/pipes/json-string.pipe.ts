import { Pipe, PipeTransform } from '@angular/core';

/**
 * Pipe che parsa una stringa in un oggetto con JSON.parse se possibile e lo riformatta in un pretty JSON
 * (con la giusta struttura e indentazione)
 */
@Pipe({
  name: 'jsonString'
})
export class JsonStringPipe implements PipeTransform {

  transform(value: string): string {
    try {
      const obj = JSON.parse(value);
      return JSON.stringify(obj, null, 2);
    } catch (err) {
      // Impossibile parsare, ritorna la stringa
      return value;
    }
  }
}
