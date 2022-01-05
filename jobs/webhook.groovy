/* groovylint-disable CompileStatic, NoDef, UnusedVariable, VariableName, VariableTypeRequired */
@Library('jenkins-library@master') _

parsedWebhookPayload = ''

pipeline {
    agent {
        kubernetes {
            idleMinutes 10
            yaml """
kind: Pod
spec:
  containers:
  - name: ansible
    image: docker.io/niiku/ansible-runner-openshift:v2.9-inc-4
    command:
    - /entrypoint.sh
    - /bin/sh
    - -c
    args:
    - cat
    tty: true
"""
        }
    }

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
                    def hostGroupCreateRegex = /[^a-zA-Z0-9]/
                    def namespace = parsedWebhookPayload['namespace'].replaceAll(hostGroupCreateRegex, "_")
                    def cluster = parsedWebhookPayload['cluster'].replaceAll(hostGroupCreateRegex, "_")
                    currentBuild.description = "${cluster}.${namespace}"
                    echo "Namespace: ${namespace}; Cluster: ${cluster}"
                    git branch: 'main', url: 'https://github.com/baloise-incubator/vmc-ansible.git'
                    container(name: 'ansible') {
                        withVault([vaultSecrets: [[path: '/secret/automation/ssh-key', secretValues: [[envVar: 'ANSIBLE_SSH_KEY', vaultKey: 'ssh-key']]]]]) {
                            sh (script: '#!/bin/sh -e\necho "$ANSIBLE_SSH_KEY" > id_rsa && chmod 600 id_rsa',returnStdout: true)
                            sh "ansible-playbook -i ansible/kubevirt.yaml ansible/generic-webhook.yaml --extra-vars \"ansible_ssh_private_key_file=id_rsa\" -l namespace_${cluster} -l label_cluster_${cluster}"
                            sh 'rm id_rsa'
                        }
                    }
                }
            }
        }
    }
}
