using System.Net;
using Microsoft.AspNetCore.Mvc.Testing;
using Xunit;

namespace SwipeHire.Api.Tests;

public sealed class EndpointAuthorizationTests
    : IClassFixture<WebApplicationFactory<Program>>
{
    private readonly WebApplicationFactory<Program> _factory;

    public EndpointAuthorizationTests(WebApplicationFactory<Program> factory)
    {
        _factory = factory;
    }

    [Theory]
    [InlineData("POST", "/api/jobs")]
    [InlineData("POST", "/api/companies")]
    [InlineData("POST", "/api/location/geocode")]
    [InlineData("GET", "/api/users/test-user/saved")]
    [InlineData("PUT", "/api/users/test-user/settings")]
    public async Task ProtectedEndpointsRejectRequestsWithoutFirebaseToken(
        string method,
        string path)
    {
        using var client = _factory.CreateClient();
        using var request = new HttpRequestMessage(new HttpMethod(method), path);

        using var response = await client.SendAsync(request);

        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }
}
