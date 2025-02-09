(ns painmeel.core
  (:require [clj-http.client :as http]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io])
  (:import (org.jsoup Jsoup))
  (:gen-class))

(defn fetch-html
  "Hello google sheet, we would like to parse you!"
  [url]
  (-> (http/get url)
      :body
      Jsoup/parse))

(defn valid-row?
  "Blabla we don't want to get stuck in an infinite loop, we we check if a row is non empty"
  [row]
  (some #(not (empty? %)) row))

(defn trim-row-to-length
  "Da fok why there are so many column? we will check the numbers based on the first row"
  [row column-length]
  (take column-length row))

(defn extract-table-data
  "Extract rows and columns, ignores empty rows, removes extra columns, and we uses the first row to
  determine column lengths."
  [html-doc]
  (let [table (.selectFirst html-doc "table")
        raw-rows (when table
                   (for [row (.select table "tr")]
                     (map #(.text %) (.select row "td, th"))))]
    (when (seq raw-rows) ;; Check Nb of rows
      (let [column-descriptions (first raw-rows) ;; We use the first one to check the Nb of column
            column-length (count column-descriptions)]
        ;; Remove empty shit, trim then prepend headers
        (->> raw-rows
             (filter valid-row?)
             (map #(trim-row-to-length % column-length)))))))

(defn write-csv
  "Write data file"
  [data output-file]
  (with-open [writer (io/writer output-file)]
    (csv/write-csv writer data)))

(defn -main
  [& args]
  (if (< (count args) 2)
    (println "Usage: lein run <URL> <output-file>")
    (let [url (nth args 0)
          output-file (nth args 1)]
      (try
        (let [html-doc (fetch-html url)
              table-data (extract-table-data html-doc)]
          (if (and table-data (not (empty? table-data)))
            (do
              (write-csv table-data output-file)
              (println "Data exported to" output-file))
            (println "No valid data found in url.")))
        (catch Exception e
          (println "Error encountered:" (.getMessage e)))))))
