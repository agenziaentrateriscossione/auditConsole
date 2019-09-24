import { ObjectEntriesPipe } from './object-entries.pipe';
import { NgModule } from '@angular/core';
import { JsonStringPipe } from './json-string.pipe';

@NgModule({
  declarations: [
    ObjectEntriesPipe,
    JsonStringPipe
  ],
  imports: [ ],
  providers: [ ],
  exports: [
    ObjectEntriesPipe,
    JsonStringPipe
  ]
})
export class CommonPipesModule { }
