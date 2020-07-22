package org.cloudfoundry.multiapps.mta.handlers;

import static java.text.MessageFormat.format;

import java.util.Map;

import org.cloudfoundry.multiapps.mta.Messages;
import org.cloudfoundry.multiapps.mta.handlers.v2.DescriptorHandler;
import org.cloudfoundry.multiapps.mta.handlers.v2.DescriptorMerger;
import org.cloudfoundry.multiapps.mta.handlers.v2.DescriptorParser;
import org.cloudfoundry.multiapps.mta.handlers.v2.DescriptorValidator;
import org.cloudfoundry.multiapps.mta.model.DeploymentDescriptor;
import org.cloudfoundry.multiapps.mta.resolvers.PlaceholderResolver;
import org.cloudfoundry.multiapps.mta.resolvers.Resolver;
import org.cloudfoundry.multiapps.mta.resolvers.ResolverBuilder;

public class HandlerFactory implements HandlerConstructor {

    public static final Integer HIGHEST_VERSION_INPUT = Integer.MAX_VALUE;

    protected int majorVersion;
    protected HandlerConstructor handlerDelegate;

    public HandlerFactory(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    protected HandlerConstructor getHandlerDelegate() {
        if (handlerDelegate == null) {
            initDelegates();
        }
        return handlerDelegate;
    }

    protected void initDelegates() {
        switch (majorVersion) {
            case 1:
            case 2:
                initV2Delegates();
                break;
            case Integer.MAX_VALUE: // HandlerFactory.MAX_VERSION_SUPPORTED_INPUT
            case 3:
                initV3Delegates();
                break;
            default:
                throw new UnsupportedOperationException(format(Messages.UNSUPPORTED_VERSION, majorVersion));
        }
    }

    protected void initV2Delegates() {
        handlerDelegate = new org.cloudfoundry.multiapps.mta.handlers.v2.HandlerConstructor();
    }

    protected void initV3Delegates() {
        handlerDelegate = new org.cloudfoundry.multiapps.mta.handlers.v3.HandlerConstructor();
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    @Override
    public DescriptorParser getDescriptorParser() {
        return getHandlerDelegate().getDescriptorParser();
    }

    @Override
    public DescriptorHandler getDescriptorHandler() {
        return getHandlerDelegate().getDescriptorHandler();
    }

    @Override
    public DescriptorMerger getDescriptorMerger() {
        return getHandlerDelegate().getDescriptorMerger();
    }

    @Override
    public DescriptorValidator getDescriptorValidator() {
        return getHandlerDelegate().getDescriptorValidator();
    }

    @Override
    public PlaceholderResolver<DeploymentDescriptor>
           getDescriptorPlaceholderResolver(DeploymentDescriptor mergedDescriptor, ResolverBuilder propertiesResolver,
                                            ResolverBuilder parametersResolver, Map<String, String> singularToPluralMapping) {
        return getHandlerDelegate().getDescriptorPlaceholderResolver(mergedDescriptor, propertiesResolver, parametersResolver,
                                                                     singularToPluralMapping);
    }

    @Override
    public Resolver<DeploymentDescriptor> getDescriptorReferenceResolver(DeploymentDescriptor mergedDescriptor,
                                                                         ResolverBuilder modulesPropertiesResolverBuilder,
                                                                         ResolverBuilder resourcePropertiesResolverBuilder,
                                                                         ResolverBuilder requiredDependenciesPropertiesResolverBuilder) {
        return getHandlerDelegate().getDescriptorReferenceResolver(mergedDescriptor, modulesPropertiesResolverBuilder,
                                                                   resourcePropertiesResolverBuilder,
                                                                   requiredDependenciesPropertiesResolverBuilder);
    }

}