crowd-pulse-sentiment-sentiwordnet
==================================

SentiWordNet and MultiWordNet based Crowd Pulse message sentiment analysis plugin.

----------------------------------

The `sentiwordnet` plugin uses both MultiWordNet and SentiWordNet to provide a sentiment value for 
messages. Therefore, you need a few file in the class loader accessible resources directory:

- `LANGUAGE_index` [MultiWordNet](http://multiwordnet.fbk.eu/english/home.php) files, where
`LANGUAGE` is the two-characters code for the language you want to support (you can fetch English 
and Italian indexes [here](https://github.com/frapontillo/multiwordnet-simple)).
- `sentiwordnet` is the [SentiWordNet](http://sentiwordnet.isti.cnr.it/) file, containing mappings 
from WordNet synsets to sentiment values.
  
## License

```
  Copyright 2015 Francesco Pontillo

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.

```