(ns character-sheet.duct.datomic
  (:require [datomic.api :as d]))

(defn seed-post-inflate
  [x]
  (assoc x :db/id (d/tempid :db.part/user)))
