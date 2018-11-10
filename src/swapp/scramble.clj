(ns swapp.scramble)

(defn scramble-test [str]
  (let [ltr-freqs (frequencies str)]
    (fn [r k v]
      (if r
        (>= (get ltr-freqs k 0) v)
        false))))

#_(defn scramble?
    "Can str be created from letters in tst-str?"
    [tst-str word]
    (let [_ (println "HELLLO WORLD" tst-str word)
          c (> (count word) (count tst-str))
          found (if c false
                    (reduce-kv (scramble-test tst-str) true (frequencies word)))
          _ (println "Found it? " found )]
      found))

(defn scramble?
  "Can str be created from letters in tst-str?"
  [tst-str word]

  (if (> (count word) (count tst-str))
    false
    (reduce-kv (scramble-test tst-str) true (frequencies word))))

(defn scramble
  "wrap scramble? so we can display a string response."
  [tst-str word]
  (if (scramble? tst-str word) "True" "False"))
