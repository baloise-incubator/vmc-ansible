# Useful commands
Get hosts and groups
```bash
ansible -i kubevirt.yaml all -m debug -a 'var=groups'
```
List all hosts
```bash
ansible -i kubevirt.yaml "*" --list-hosts
```

Run playbook
```bash
ansible-playbook -i kubevirt.yaml playbooks/default.yaml --extra-vars "ansible_ssh_private_key_file=~/.ssh/kubevirt_id_rsa"
