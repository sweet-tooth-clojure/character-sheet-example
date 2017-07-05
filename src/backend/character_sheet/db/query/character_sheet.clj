(ns character-sheet.db.query.character-sheet
  (:require [datomic.api :as d]))

(def character-sheet-attrs
  [:db/id
   :character-sheet/name
   :character-sheet/level
   :character-sheet/story
   :character-sheet/strength
   :character-sheet/motivation
   :character-sheet/attention-span
   :character-sheet/willpower
   :character-sheet/cunning
   :character-sheet/endurance
   :character-sheet/imagination])

(defn character-sheets
  [db]
  (with-meta
    (->> (d/q {:find [(list 'pull '?e character-sheet-attrs)]
               :in '[$]
               :where [['?e :character-sheet/name]]}
              db)
         (map first))
    {:ent-type :character-sheet}))

(defn character-sheet
  [db id]
  (-> (d/pull db character-sheet-attrs id)
      (with-meta {:ent-type :character-sheet})))

