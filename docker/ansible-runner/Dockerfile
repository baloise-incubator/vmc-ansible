FROM quay.io/ansible/ansible-runner:stable-2.9-devel
RUN pip3 install openshift
ENV INITIAL_CONFIG=/tmp/initial_config

# Change permissions to let any arbitrary user
RUN for f in "${HOME}" "/etc/passwd" "/etc/group"; do \
    echo "Changing permissions on ${f}" && chgrp -R 0 ${f} && \
    chmod -R g+rwX ${f}; \
    done

COPY entrypoint.sh /entrypoint.sh
USER 1001
ENTRYPOINT [ "/entrypoint.sh" ]