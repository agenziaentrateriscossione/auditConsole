const jsonConcat = require('json-concat');

jsonConcat({
  src: "src/mocks/data",
  dest: "src/mocks/data.json",
}, json => {
  console.log(json);
});
