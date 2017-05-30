(ns runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [larch.core-test]))

(doo-tests 'larch.core-test)
