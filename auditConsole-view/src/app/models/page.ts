import SearchForm from './search-form';
import AuditTitle from './audit-title';

export default class Page {
  last: boolean;
  first: boolean;
  totalPages: number;
  totalElements: number;
  numberOfElements: number;
  number: number;
  size: number;
  filters: SearchForm = new SearchForm();
  content: AuditTitle[] = [];
  scrollbarPosition: number;
  newQuery: boolean = false;

  static deserialize(pageJson: any): Page {
    const pageList = new Page();
    pageList.last = pageJson.last;
    pageList.first = pageJson.first;
    pageList.totalPages = pageJson.totalPages;
    pageList.totalElements = pageJson.totalElements;
    pageList.numberOfElements = pageJson.numberOfElements;
    pageList.number = pageJson.number;
    pageList.size = pageJson.size;
    pageList.content = Page.deserializeAllAudits(pageJson.content);
    pageList.scrollbarPosition = pageJson.scrollbarPosition;
    return pageList;
  }

  static deserializeAllAudits(resultArray: any): AuditTitle[] {
    const auditTitles: AuditTitle[] = [];
    for (let result of resultArray) {
      auditTitles.push(AuditTitle.deserialize(result));
    }
    return auditTitles;
  }
}
