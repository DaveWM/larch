(defproject larch "0.1.0-SNAPSHOT"
  :description "An elm-like clojurescript framework"
  :url "An elm-like clojurescript framework"
  :license {:name "GPL V3"
            :url "https://www.gnu.org/licenses/gpl-3.0.en.html"}
  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-doo "0.1.7"]]
  :dependencies [[org.clojure/clojure "1.9.0-alpha15"]
                 [org.clojure/clojurescript "1.9.494"]
                 [org.clojure/core.async "0.3.442"]]
  :cljsbuild {
              :builds [{:id "prod"
                        :source-paths ["src"]
                        :compiler {
                                   :output-to "dist/main.min.js"
                                   :optimizations :advanced
                                   :pretty-print true}}
                       {:id "test"
                        :source-paths ["src" "test"]
                        :compiler {:output-to "main/testable.js"
                                   :main runner
                                   :optimizations :none}}]})
