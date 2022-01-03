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
                    def eventType = "$X-KubeVirt-Event"
                    echo eventType
                    echo parsedWebhookPayload
                }
            }
        }
    }
}
