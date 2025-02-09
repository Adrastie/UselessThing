(defproject painmeel "0.1.0-SNAPSHOT"
  :description "Save a click, adopt a lars"
  :url "http://painmeel.lan.lars"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                  [clj-http "3.12.3"]                 ;; HTTP client
                  [org.jsoup/jsoup "1.15.4"]          ;; Parsing
                  [org.clojure/data.csv "1.0.1"]]     ;; CSV
  :main ^:skip-aot painmeel.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
