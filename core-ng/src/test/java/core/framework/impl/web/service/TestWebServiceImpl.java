package core.framework.impl.web.service;

import java.util.List;

/**
 * @author neo
 */
public class TestWebServiceImpl implements TestWebService {
    @Override
    public TestResponse get(Integer id) {
        return null;
    }

    @Override
    public void create(Integer id, TestRequest request) {

    }

    @Override
    public void delete(String id) {

    }

    @Override
    public List<TestResponse> batch(List<TestRequest> requests) {
        return null;
    }
}
