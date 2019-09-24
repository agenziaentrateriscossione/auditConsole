export default class GenericUtils {

  /**
   * Esegue il merge univoco (nessuna ripetizione) di due array di tipo qualsiasi, eseguendo il confronto
   * sulla campo passato come parametro.
   * @param {any[]} array1 - lista merge 1
   * @param {any[]} array2 - lista merge 2
   * @param {string} field - campo identificativo
   */
  static uniqueMerge(array1: any[], array2: any[], field: string): any[] {
    const result: any[] = [];
    const arrayConcat = array1.concat(array2);
    const assoc = {};
    for (const item of arrayConcat) {
      if (!assoc[item[field]]) {
        result.push(item);
        assoc[item[field]] = true;
      }
    }
    return result;
  }

}
