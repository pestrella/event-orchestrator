;; Author: Paolo Estrella
(ns noble.orchestrator
  (import [com.thisisnoble.javatest Orchestrator]
          [com.thisisnoble.javatest.impl CompositeEvent]))


(def processors (atom []))
(def publisher (atom nil))
(def events (atom {}))

#_(.size (@events "tradeEvt"))

(defn debug [event]
  (println (str "Recieved event: ("
                (.getId event)
                (when-let [parent-id (.getParentId event)]
                  (str "<" parent-id))
                ")")))

(defn collate [events event]
  (let [event-id (.getId event)
        parent-id (.getParentId event)
        parent (get events parent-id)]

    (let [parent (when parent (doto parent
                                (.addChild event)))]
      (assoc (if parent-id
               (if parent
                 (assoc events parent-id parent)
                 (assoc events parent-id (CompositeEvent. parent-id nil)))
               events)
        event-id
        (CompositeEvent. event-id parent)))))

(def orchestrator
  (reify Orchestrator
    (register [this processor]
      (swap! processors conj processor))
    (receive [this event]
      (do
        (debug event)
        (swap! events collate event)
        (when-let [parent (@events (.getParentId event))]
          (println "publishing..." parent)
          (.publish @publisher parent))
        (doseq [processor @processors]
          (when (.interestedIn processor event)
            (.process processor event)))))
    (setup [this pub]
      (reset! publisher pub))))

(import '[com.thisisnoble.javatest SimpleOrchestratorTest])
(doto (SimpleOrchestratorTest. )
  (.setOrchestrator orchestrator)
  (.tradeEventShouldTriggerAllProcessors))
