(ns ephemeris.core
  (:require [ephemeris.points :refer (lookup known?)])
  (:import (swisseph SwissEph SweDate SweConst)))

(def defaults {:utc nil
               :geo {:lat nil :lon nil}
               :bodies []
               :angles []
               :houses false})

(defn calc-now [stuff]
  (let [want (merge defaults stuff)
        sw (SwissEph.)
        sd (SweDate.)
        jd (.getJulDay sd)
        flag (. SweConst SEFLG_SPEED)]
    (for [i (flatten [(:bodies want)])]
      (let [what (if (known? i) (lookup i) i)
            res (double-array 6)
            err (StringBuffer.)
            rc (.swe_calc_ut sw jd
                what
                flag
                res
                err)]
        {(lookup what) {:lon (aget res 0)
                        :lat (aget res 1)
                        :sdd (aget res 3)}}))))