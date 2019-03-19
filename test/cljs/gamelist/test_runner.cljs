(ns gamelist.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [gamelist.core-test]
   [gamelist.common-test]))

(enable-console-print!)

(doo-tests 'gamelist.core-test
           'gamelist.common-test)
