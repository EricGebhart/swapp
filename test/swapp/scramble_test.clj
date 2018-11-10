(ns swapp.scramble-test
  (:require [swapp.scramble :as sut]
            [midje.sweet :refer [:all]]))


(tabular
 (facts "scramble finds if the necessary letters of a word are in a scrambled list of letters."
        (fact "scramble? gives us true or false."
              (sut/scramble? ?scramble ?word) => ?result))

 ?scramble                  ?word            ?result
 "foobar"                   "bar"            true
 "foobar"                   "foobar"         true
 "rekqodlw"                 "world"          true
 "cedewaraaossoqqyt"        "codewars"       true
 "katas"                    "steak"          false
 "Foobar"                   "foob"           false
 "FoObar"                   "FOob"           true
 "ao\",.peusnth!@#%$)(*&"   "a@!"            true
 "ao\",.peusnth!@#%$)(*&"   "a@!z"           false
 "ao\",.peusnth!@#%$)(*&"   "a@!S"           false
 )
