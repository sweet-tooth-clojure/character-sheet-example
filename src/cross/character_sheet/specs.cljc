(ns character-sheet.specs
  (:require [clojure.spec :as s]))

(defn validate
  ([spec value]
   (validate spec (str "Spec validation failed for " spec " spec") value))
  ([spec error-message value]
   (if-not (s/valid? spec value)
     (throw (ex-info error-message
                     (s/explain-data spec value)))
     value)))

(s/def :common/not-empty-string (s/and string? not-empty))
