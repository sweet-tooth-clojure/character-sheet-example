(ns character-sheet.routes)

(def routes ["/" {"" :home
                  ["character-sheet/" :character-sheet-id] :show-character-sheet}])
