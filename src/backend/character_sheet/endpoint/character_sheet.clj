(ns character-sheet.endpoint.character-sheet
  (:require [integrant.core :as ig]
            [character-sheet.endpoint.common :as lc]
            [sweet-tooth.endpoint.liberator :as el]
            [sweet-tooth.endpoint.page :as epg]
            [sweet-tooth.endpoint.datomic :as ed]
            [compojure.core :refer :all]
            [character-sheet.db.query.character-sheet :as qcs]))

(def validation
  {:character-sheet/name ["Please enter a name" not-empty]})

(defn decisions
  [component]
  (lc/initialize-decisions
    component
    {:list {:handle-ok (fn [ctx]
                         [(->> (qcs/character-sheets (lc/db ctx))
                               (epg/paginate (epg/page-params ctx)))])}
     :show {:handle-ok #(-> (qcs/character-sheet (lc/db %) (el/ctx-id %))
                            lc/format-ent)}

     :update {:put! (fn [ctx]
                      (-> @(ed/update ctx)
                          (el/->ctx :result)))
              :handle-ok #(-> (qcs/character-sheet (ed/db-after %) (el/ctx-id %))
                              lc/format-ent)}

     :delete {:delete! (comp deref ed/delete)}
     
     :create {:malformed? (el/validator validation)
              :post! #(-> @(ed/create (update-in % [:request :params] dissoc :page))
                          (el/->ctx :result))
              :handle-created (fn [ctx]
                                [(->> (qcs/character-sheets (ed/db-after ctx))
                                      (epg/paginate (assoc (:page (el/params ctx)) :page 1)))])}}))

(def endpoint (lc/endpoint "/api/v1/character-sheet" decisions))

(defmethod ig/init-key :character-sheet.endpoint/character-sheet [_ options]
  (endpoint options))
