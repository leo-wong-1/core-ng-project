package core.framework.impl.web.service;

import core.framework.api.http.HTTPMethod;
import core.framework.api.util.Exceptions;
import core.framework.api.util.Sets;
import core.framework.api.util.Strings;
import core.framework.api.web.service.DELETE;
import core.framework.api.web.service.GET;
import core.framework.api.web.service.POST;
import core.framework.api.web.service.PUT;
import core.framework.api.web.service.Path;
import core.framework.api.web.service.PathParam;
import core.framework.impl.validate.type.JAXBTypeValidator;
import core.framework.impl.web.bean.BeanValidator;
import core.framework.impl.web.route.PathPatternValidator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * @author neo
 */
public class WebServiceInterfaceValidator {
    private final Class<?> serviceInterface;
    private final BeanValidator validator;

    public WebServiceInterfaceValidator(Class<?> serviceInterface, BeanValidator validator) {
        this.serviceInterface = serviceInterface;
        this.validator = validator;
    }

    public void validate() {
        if (!serviceInterface.isInterface())
            throw Exceptions.error("service interface must be interface, serviceInterface={}", serviceInterface.getCanonicalName());

        for (Method method : serviceInterface.getDeclaredMethods()) {
            validate(method);
        }
    }

    private void validate(Method method) {
        validateHTTPMethod(method);

        HTTPMethod httpMethod = HTTPMethodHelper.httpMethod(method);

        Path path = method.getDeclaredAnnotation(Path.class);
        if (path == null) throw Exceptions.error("method must have @Path, method={}", method);
        new PathPatternValidator(path.value()).validate();

        validateResponseBeanType(method.getGenericReturnType());

        Set<String> pathVariables = pathVariables(path.value());
        Type requestBeanType = null;

        Annotation[][] annotations = method.getParameterAnnotations();
        Type[] paramTypes = method.getGenericParameterTypes();
        Set<String> pathParams = Sets.newHashSet();

        for (int i = 0; i < paramTypes.length; i++) {
            Type paramType = paramTypes[i];
            PathParam pathParam = pathParam(annotations[i]);
            if (pathParam != null) {
                validatePathParamType(paramType);
                pathParams.add(pathParam.value());
            } else {
                if (requestBeanType != null)
                    throw Exceptions.error("service method must not have more than one bean param, previous={}, current={}", requestBeanType.getTypeName(), paramType.getTypeName());
                requestBeanType = paramType;

                if (httpMethod == HTTPMethod.GET || httpMethod == HTTPMethod.DELETE) {
                    validator.registerQueryParamBeanType(requestBeanType);
                } else {
                    validator.registerRequestBeanType(requestBeanType);
                }
            }
        }

        if (pathVariables.size() != pathParams.size() || !pathVariables.containsAll(pathParams))
            throw Exceptions.error("service method @PathParam params must match variable in path pattern, path={}, method={}", path.value(), method);
    }

    private Set<String> pathVariables(String path) {
        Set<String> names = Sets.newHashSet();
        String[] tokens = Strings.split(path, '/');
        for (String token : tokens) {
            if (token.startsWith(":")) {
                int paramIndex = token.indexOf('(');
                int endIndex = paramIndex > 0 ? paramIndex : token.length();
                boolean isNew = names.add(token.substring(1, endIndex));
                if (!isNew) throw Exceptions.error("path must not have duplicate param name, path={}", path);
            }
        }
        return names;
    }

    private PathParam pathParam(Annotation[] paramAnnotations) {
        for (Annotation paramAnnotation : paramAnnotations) {
            if (paramAnnotation instanceof PathParam) return (PathParam) paramAnnotation;
        }
        return null;
    }

    private void validatePathParamType(Type paramType) {
        if (!(paramType instanceof Class))
            throw Exceptions.error("path param must be class, type={}", paramType.getTypeName());

        Class<?> paramClass = (Class<?>) paramType;

        if (paramClass.isPrimitive())
            throw Exceptions.error("primitive class is not supported, please use object class, paramClass={}", paramClass);

        if (Integer.class.equals(paramClass)) return;
        if (Long.class.equals(paramClass)) return;
        if (String.class.equals(paramClass)) return;
        if (paramClass.isEnum()) {
            @SuppressWarnings("unchecked")
            Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) paramClass;
            JAXBTypeValidator.validateEnumClass(enumClass);
            return;
        }
        throw Exceptions.error("path param class is not supported, paramClass={}", paramClass);
    }

    private void validateResponseBeanType(Type responseBeanType) {
        if (void.class == responseBeanType) return;
        validator.validateResponseBeanType(responseBeanType);
    }

    private void validateHTTPMethod(Method method) {
        int count = 0;
        if (method.isAnnotationPresent(GET.class)) count++;
        if (method.isAnnotationPresent(POST.class)) count++;
        if (method.isAnnotationPresent(PUT.class)) count++;
        if (method.isAnnotationPresent(DELETE.class)) count++;
        if (count != 1)
            throw Exceptions.error("method must have exact one http method annotation, method={}", method);
    }
}
