/* groovylint-disable CompileStatic, NoDef, UnusedVariable, VariableName, VariableTypeRequired */
@Library('jenkins-library@master') _

parsedWebhookPayload = ''

pipeline {
    agent any

    triggers {
        GenericTrigger(
                causeString: 'Generic Cause',
                genericVariables: [[defaultValue: '', key: 'webhookPayload', regexpFilter: '', value: '$']],
                genericHeaderVariables: [[key: 'X-KubeVirt-Event', regexpFilter: '']],
                regexpFilterExpression: '',
                regexpFilterText: '',
                token: 'kubevirt-update'
        )
    }

    stages {
        stage('Perform update on node') {
            steps {
                script {
                    parsedWebhookPayload = readJSON text: "${webhookPayload}"
                    def eventType = "$X_KubeVirt_Event"
                    echo "Event Type: ${eventType}"
                    def hostGroupCreateRegex = /[^a-zA-Z0-9 -]/
                    def namespace = parsedWebhookPayload['namespace'].replaceAll(hostGroupCreateRegex, "_")
                    def cluster = parsedWebhookPayload['cluster'].replaceAll(hostGroupCreateRegex, "_")
                    echo "Namespace: ${namespace}; Cluster: ${cluster}"
                }
            }
        }
    }
}
