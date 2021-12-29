podTemplate(idleMinutes: 10, yaml: """
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
) {

    node(POD_LABEL) {
        stage('Run ansible') {
            git branch: 'main', url: 'https://github.com/baloise-incubator/vmc-ansible.git'
            container(name: 'ansible') {
                withVault([vaultSecrets: [[path: '/secret/automation/ssh-key', secretValues: [[envVar: 'ANSIBLE_SSH_KEY', vaultKey: 'ssh-key']]]]]) {
                    sh (script: '#!/bin/sh -e\necho "$ANSIBLE_SSH_KEY" > id_rsa && chmod 600 id_rsa',returnStdout: true)
                    sh 'pwd'
                    sh 'ls -lah'
                    sh 'ansible-playbook -i kubevirt.yaml playbooks/default.yaml --extra-vars "ansible_ssh_private_key_file=id_rsa"'
                    sh 'rm id_rsa'
                }
            }
        }
    }
}