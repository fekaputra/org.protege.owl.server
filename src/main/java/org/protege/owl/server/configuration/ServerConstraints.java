package org.protege.owl.server.configuration;

import static org.protege.owl.server.configuration.MetaprojectVocabulary.HAS_TRANSPORT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.protege.owl.server.api.Server;
import org.protege.owl.server.api.ServerComponentFactory;
import org.protege.owl.server.api.ServerTransport;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

public class ServerConstraints {
	private OWLIndividual serverIndividual;

	private FilterConstraint containingFilterConstraint;
	private List<TransportConstraints> transportConstraints = new ArrayList<TransportConstraints>();
	
	public ServerConstraints(OWLOntology configuration, OWLIndividual serverIndividual) {
		this.serverIndividual = serverIndividual;
		containingFilterConstraint = FilterConstraint.getDelegateConstraint(configuration, serverIndividual);
		Set<OWLIndividual> transports = serverIndividual.getObjectPropertyValues(HAS_TRANSPORT, configuration);
		if (transports != null) {
			for (OWLIndividual transport : transports) {
				transportConstraints.add(new TransportConstraints(configuration, transport));
			}
		}
	}


	public boolean satisfied(Set<ServerComponentFactory> factories) {
	    if (containingFilterConstraint != null && !containingFilterConstraint.satisfied(factories)) {
	        return false;
	    }
		for (TransportConstraints constraint : transportConstraints) {
			if (!constraint.satisfied(factories)) {
				return false;
			}
		}
		
		for (ServerComponentFactory factory : factories) {
			if (factory.hasSuitableServer(serverIndividual)) {
				return true;
			}
		}
		return false;
	}


	public List<ServerTransport> buildServerTransports(Set<ServerComponentFactory> factories, Server server) throws IOException {
		List<ServerTransport> transports = new ArrayList<ServerTransport>();
		for (TransportConstraints constraint : transportConstraints) {
			ServerTransport transport = constraint.build(factories);
			transports.add(transport);
			transport.start(server);
		}
		return transports;
	}
	
	public Server buildServer(Set<ServerComponentFactory> factories) {
	    Server baseServer = null;
		for (ServerComponentFactory factory : factories) {
			if (factory.hasSuitableServer(serverIndividual)) {
				baseServer = factory.createServer(serverIndividual);
			}
		}
		if (baseServer == null) {
		    throw new IllegalStateException("Expected to be ready to build the server...");
		}
		else if (containingFilterConstraint != null) {
		    return containingFilterConstraint.build(baseServer, factories);
		}
		else {
		    return baseServer;
		}
	}
	

}