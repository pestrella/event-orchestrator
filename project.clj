(defproject event-orchestrator "0.1.0-SNAPSHOT"
  :description "An event orchestrator demo."
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [junit/junit "4.11"]]
  :source-paths ["src/clojure" "test/clojure"]
  :java-source-paths ["src/java" "test/java"]
  :java-options ["-target" "1.6" "-source" "1.6"])
