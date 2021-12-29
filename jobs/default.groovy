podTemplate(yaml: """
kind: Pod
spec:
  containers:
  - name: ansible
    image: docker.io/niiku/ansible-runner-openshift:v2.11
    command:
    - /bin/sh 
    - -c
    args:
    - cat
    tty: true
"""
) {

    node(POD_LABEL) {
        stage('Run ansible') {
            container(name: 'ansible') {
                withVault([vaultSecrets: [[path: '/secret/automation/ssh-key', secretValues: [[envVar: 'ANSIBLE_SSH_KEY', vaultKey: 'ssh-key']]]]]) {
                    sh 'ssh-add - <<< "$ANSIBLE_SSH_KEY"'
                }
                sh 'ansible-playbook -i kubevirt.yaml playbooks/default.yaml'
            }
        }
    }
}