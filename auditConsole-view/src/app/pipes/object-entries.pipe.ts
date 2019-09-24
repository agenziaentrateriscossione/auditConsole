import { Pipe, PipeTransform } from '@angular/core';

/**
 * Pipe che parsa un oggetto in un array di entries chiave, valore
 */
@Pipe({
  name: 'entries'
})
export class ObjectEntriesPipe implements PipeTransform {

  transform(value: object) : any {
    const keys = [];
    for (let key in value) {
      keys.push({key: key, value: value[key]});
    }
    return keys;
  }

}
