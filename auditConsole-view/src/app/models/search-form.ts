import { NgbDateStruct, NgbTimeStruct } from '@ng-bootstrap/ng-bootstrap';

export default class SearchForm {
  archivio: string;
  tipoRecord: string;
  idRecord: string;
  tipoAzione: string[];
  codUser: string;
  username: string;
  dateFrom: NgbDateStruct;
  timeFrom: NgbTimeStruct;
  dateTo: NgbDateStruct;
  timeTo: NgbTimeStruct;

  archives: string[];
  recordTypes: string[];
  actions: string[];
}
