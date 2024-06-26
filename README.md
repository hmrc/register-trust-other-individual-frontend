
# Register Trust Other Individual Frontend

This service is responsible for collecting details about any other individuals associated with the trust when registering a trust.

To run locally using the micro-service provided by the service manager:

```bash
sm2 --start TRUSTS_ALL
```

or

```bash
sm2 --start REGISTER_TRUST_ALL
```

---

If you want to run your local copy, then stop the frontend ran by the service manager and run your local code by using the following (port number is 8841 but is defaulted to that in build.sbt):

```bash
sbt run
```

---

## Testing the service
Run unit tests before raising a PR to ensure your code changes pass the Jenkins pipeline. This runs all the unit tests and integration tests with scalastyle and checks for dependency updates:

```bash
./run_all_tests.sh
```

### UI Tests
Start up service in SM2 as shown above then:

```bash
./run_local_register_other_individual.sh
```
from trusts-acceptance-tests repository.

---

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
