(ns character-sheet.duct.datomic
  (:require [integrant.core :as ig]))

(defmethod ig/init-key :character-sheet.duct/datomic [_ spec]
  spec)
