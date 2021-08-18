/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package navigation

import base.SpecBase
import config.FrontendAppConfig
import controllers.register.individual.mld5.routes._
import controllers.register.individual.routes._
import controllers.register.routes._
import generators.Generators
import models.Status.Completed
import models._
import models.register.pages.AddOtherIndividual
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.entitystatus.OtherIndividualStatus
import pages.register.individual._
import pages.register.individual.mld5._
import pages.register.{AddOtherIndividualPage, AddOtherIndividualYesNoPage, TrustHasOtherIndividualYesNoPage}
import play.api.mvc.Call
import utils.Constants.ES

class OtherIndividualNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private val navigator = new OtherIndividualNavigator(frontendAppConfig)
  private val index = 0
  private val nino: String = "nino"

  private def otherIndividualsCompletedRoute(draftId: String, config: FrontendAppConfig): Call = {
    Call("GET", config.registrationProgressUrl(draftId))
  }

  "OtherIndividual navigator" must {

    "a 4mld trust" must {

      val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = false, isTaxable = true)

      "AddOtherIndividualYesNoPage -> Yes -> NamePage from AddOtherIndividualYesNoPage" in {
        val answers = baseAnswers.set(AddOtherIndividualYesNoPage, true).success.value

        navigator.nextPage(AddOtherIndividualYesNoPage, fakeDraftId, answers)
          .mustBe(NameController.onPageLoad(index, fakeDraftId))
      }

      "AddOtherIndividualYesNoPage -> No -> RegistrationProgress" in {
        val answers = baseAnswers.set(AddOtherIndividualYesNoPage, false).success.value

        navigator.nextPage(AddOtherIndividualYesNoPage, fakeDraftId, answers)
          .mustBe(otherIndividualsCompletedRoute(fakeDraftId, frontendAppConfig))
      }

      "AddOtherIndividualPage -> add them now" when {
        "no individuals" must {
          "-> NamePage at index 0" in {
            val answers = baseAnswers.set(AddOtherIndividualPage, AddOtherIndividual.YesNow).success.value

            navigator.nextPage(AddOtherIndividualPage, fakeDraftId, answers)
              .mustBe(NameController.onPageLoad(0, fakeDraftId))
          }
        }

        "there are individuals" must {
          "-> NamePage at next index" in {
            val answers = baseAnswers
              .set(AddOtherIndividualPage, AddOtherIndividual.YesNow).success.value
              .set(NamePage(0), FullName("First", None, "Last")).success.value
              .set(OtherIndividualStatus(0), Completed).success.value

            navigator.nextPage(AddOtherIndividualPage, fakeDraftId, answers)
              .mustBe(NameController.onPageLoad(1, fakeDraftId))
          }
        }
      }

      "AddOtherIndividualPage -> add them later -> RegistrationProgress" in {
        val answers = baseAnswers
          .set(NamePage(index), FullName("First", None, "Last")).success.value
          .set(AddOtherIndividualPage, AddOtherIndividual.YesLater).success.value

        navigator.nextPage(AddOtherIndividualPage, fakeDraftId, answers)
          .mustBe(otherIndividualsCompletedRoute(fakeDraftId, frontendAppConfig))
      }

      "AddOtherIndividualPage -> added them all -> RegistrationProgress" in {
        val answers = baseAnswers
          .set(NamePage(index), FullName("First", None, "Last")).success.value
          .set(AddOtherIndividualPage, AddOtherIndividual.NoComplete).success.value

        navigator.nextPage(AddOtherIndividualPage, fakeDraftId, answers)
          .mustBe(otherIndividualsCompletedRoute(fakeDraftId, frontendAppConfig))
      }

      "TrustHasOtherIndividualYesNoPage -> yes -> InfoPage" in {
        val answers = baseAnswers.set(TrustHasOtherIndividualYesNoPage, true).success.value

        navigator.nextPage(TrustHasOtherIndividualYesNoPage, fakeDraftId, answers)
          .mustBe(InfoController.onPageLoad(fakeDraftId))
      }

      "TrustHasOtherIndividualYesNoPage -> no -> RegistrationProgress" in {
        val answers = baseAnswers.set(TrustHasOtherIndividualYesNoPage, false).success.value

        navigator.nextPage(TrustHasOtherIndividualYesNoPage, fakeDraftId, answers)
          .mustBe(otherIndividualsCompletedRoute(fakeDraftId, frontendAppConfig))
      }

      "NamePage -> DateOfBirthYesNoPage" in {
        navigator.nextPage(NamePage(index), draftId, baseAnswers)
          .mustBe(DateOfBirthYesNoController.onPageLoad(index, draftId))
      }

      "DateOfBirthYesNoPage -> Yes -> DateOfBirthPage" in {
        val answers = baseAnswers.set(DateOfBirthYesNoPage(index), true).success.value

        navigator.nextPage(DateOfBirthYesNoPage(index), draftId, answers)
          .mustBe(DateOfBirthController.onPageLoad(index, draftId))
      }

      "DateOfBirthYesNoPage -> No -> NationalInsuranceYesNoPage" in {
        val answers = baseAnswers.set(DateOfBirthYesNoPage(index), false).success.value

        navigator.nextPage(DateOfBirthYesNoPage(index), draftId, answers)
          .mustBe(NationalInsuranceYesNoController.onPageLoad(index, draftId))
      }

      "DateOfBirthPage -> NationalInsuranceYesNoPage" in {
        navigator.nextPage(DateOfBirthPage(index), draftId, baseAnswers)
          .mustBe(NationalInsuranceYesNoController.onPageLoad(index, draftId))
      }

      "NationalInsuranceYesNoPage -> Yes -> NationalInsurancePage" in {
        val answers = baseAnswers.set(NationalInsuranceYesNoPage(index), true).success.value

        navigator.nextPage(NationalInsuranceYesNoPage(index), draftId, answers)
          .mustBe(NationalInsuranceNumberController.onPageLoad(index, draftId))
      }

      "NationalInsuranceYesNoPage -> No -> AddressYesNoPage" in {
        val answers = baseAnswers.set(NationalInsuranceYesNoPage(index), false).success.value

        navigator.nextPage(NationalInsuranceYesNoPage(index), draftId, answers)
          .mustBe(AddressYesNoController.onPageLoad(index, draftId))
      }

      "NationalInsurancePage -> CheckDetailsPage" in {
        navigator.nextPage(NationalInsuranceNumberPage(index), draftId, baseAnswers)
          .mustBe(CheckDetailsController.onPageLoad(index, draftId))
      }

      "AddressYesNoPage -> Yes -> AddressUkYesNoPage" in {
        val answers = baseAnswers
          .set(AddressYesNoPage(index), true).success.value

        navigator.nextPage(AddressYesNoPage(index), draftId, answers)
          .mustBe(AddressUkYesNoController.onPageLoad(index, draftId))
      }

      "AddressYesNoPage -> No -> CheckDetailsPage" in {
        val answers = baseAnswers
          .set(AddressYesNoPage(index), false).success.value

        navigator.nextPage(AddressYesNoPage(index), draftId, answers)
          .mustBe(CheckDetailsController.onPageLoad(index, draftId))
      }

      "AddressUkYesNoPage -> Yes -> UKAddressPage" in {
        val answers = baseAnswers
          .set(AddressUkYesNoPage(index), true).success.value

        navigator.nextPage(AddressUkYesNoPage(index), draftId, answers)
          .mustBe(UkAddressController.onPageLoad(index, draftId))
      }

      "AddressUkYesNoPage -> No -> NonUKAddressPage" in {
        val answers = baseAnswers
          .set(AddressUkYesNoPage(index), false).success.value

        navigator.nextPage(AddressUkYesNoPage(index), draftId, answers)
          .mustBe(NonUkAddressController.onPageLoad(index, draftId))
      }

      "UKAddressPage -> PassportDetailsYesNoController" in {
        navigator.nextPage(UkAddressPage(index), draftId, baseAnswers)
          .mustBe(PassportDetailsYesNoController.onPageLoad(index, draftId))
      }

      "NonUKAddressPage -> PassportDetailsYesNoController" in {
        navigator.nextPage(NonUkAddressPage(index), draftId, baseAnswers)
          .mustBe(PassportDetailsYesNoController.onPageLoad(index, draftId))
      }

      "PassportDetailsYesNoPage -> Yes -> PassportDetailsPage" in {
        val answers = baseAnswers
          .set(PassportDetailsYesNoPage(index), true).success.value

        navigator.nextPage(PassportDetailsYesNoPage(index), draftId, answers)
          .mustBe(PassportDetailsController.onPageLoad(index, draftId))
      }

      "PassportDetailsYesNoPage -> No -> IDCardDetailsYesNoPage" in {
        val answers = baseAnswers
          .set(PassportDetailsYesNoPage(index), false).success.value

        navigator.nextPage(PassportDetailsYesNoPage(index), draftId, answers)
          .mustBe(IDCardDetailsYesNoController.onPageLoad(index, draftId))
      }

      "PassportDetailsPage -> CheckDetailsPage" in {
        navigator.nextPage(PassportDetailsPage(index), fakeDraftId, baseAnswers)
          .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
      }

      "IDCardDetailsYesNoPage -> Yes -> IDCardDetailsPage" in {
        val answers = baseAnswers
          .set(IDCardDetailsYesNoPage(index), true).success.value

        navigator.nextPage(IDCardDetailsYesNoPage(index), draftId, answers)
          .mustBe(IDCardDetailsController.onPageLoad(index, draftId))
      }

      "IDCardDetailsYesNoPage -> No -> CheckDetailsPage" in {
        val answers = baseAnswers
          .set(IDCardDetailsYesNoPage(index), false).success.value

        navigator.nextPage(IDCardDetailsYesNoPage(index), draftId, answers)
          .mustBe(CheckDetailsController.onPageLoad(index, draftId))
      }

      "CheckDetailsPage -> AddOtherIndividualPage" in {
        navigator.nextPage(CheckDetailsPage, draftId, baseAnswers)
          .mustBe(AddOtherIndividualController.onPageLoad(draftId))
      }

    }

    "a 5mld trust" when {

      "taxable" when {

        val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = true)

        "NamePage -> DateOfBirthYesNoPage" in {
          navigator.nextPage(NamePage(index), draftId, baseAnswers)
            .mustBe(DateOfBirthYesNoController.onPageLoad(index, draftId))
        }

        "DateOfBirthYesNoPage -> Yes -> DateOfBirthPage" in {
          val answers = baseAnswers.set(DateOfBirthYesNoPage(index), true).success.value

          navigator.nextPage(DateOfBirthYesNoPage(index), draftId, answers)
            .mustBe(DateOfBirthController.onPageLoad(index, draftId))
        }

        "Date of birth yes no page -> No -> CountryOfNationality Yes No page" in {
          val answers = baseAnswers
            .set(DateOfBirthYesNoPage(index), false).success.value

          navigator.nextPage(DateOfBirthYesNoPage(index), draftId, answers)
            .mustBe(CountryOfNationalityYesNoController.onPageLoad(index, draftId))
        }

        "Date of birth page -> CountryOfNationality Yes No page" in {
          navigator.nextPage(DateOfBirthPage(index), draftId, baseAnswers)
            .mustBe(CountryOfNationalityYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfNationality yes no page -> No -> Nino yes no page" in {
          val answers = baseAnswers
            .set(CountryOfNationalityYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfNationalityYesNoPage(index), draftId, answers)
            .mustBe(NationalInsuranceYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfNationality yes no page -> Yes -> CountryOfNationality Uk yes no page" in {
          val answers = baseAnswers
            .set(CountryOfNationalityYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfNationalityYesNoPage(index), draftId, answers)
            .mustBe(CountryOfNationalityInTheUkYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfNationalityInUK yes no page -> No -> CountryOfNationality page" in {
          val answers = baseAnswers
            .set(CountryOfNationalityInTheUkYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfNationalityInTheUkYesNoPage(index), draftId, answers)
            .mustBe(CountryOfNationalityController.onPageLoad(index, draftId))
        }

        "CountryOfNationalityInUK yes no page -> Yes -> Nino yes no page" in {
          val answers = baseAnswers
            .set(CountryOfNationalityInTheUkYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfNationalityInTheUkYesNoPage(index), draftId, answers)
            .mustBe(NationalInsuranceYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfNationality -> Nino Yes No page" in {
          val answers = baseAnswers
            .set(CountryOfNationalityPage(index), ES).success.value

          navigator.nextPage(CountryOfNationalityPage(index), draftId, answers)
            .mustBe(NationalInsuranceYesNoController.onPageLoad(index, draftId))
        }

        "NationalInsuranceYesNoPage -> Yes -> NationalInsurancePage" in {
          val answers = baseAnswers.set(NationalInsuranceYesNoPage(index), true).success.value

          navigator.nextPage(NationalInsuranceYesNoPage(index), draftId, answers)
            .mustBe(NationalInsuranceNumberController.onPageLoad(index, draftId))
        }

        "NationalInsuranceNumber yes no page -> No -> CountryOfResidence yes no page" in {
          val answers = baseAnswers
            .set(NationalInsuranceYesNoPage(index), false).success.value

          navigator.nextPage(NationalInsuranceYesNoPage(index), draftId, answers)
            .mustBe(CountryOfResidenceYesNoController.onPageLoad(index, draftId))
        }

        "NationalInsurancePage -> CountryOfResidence yes no page" in {
          navigator.nextPage(NationalInsuranceNumberPage(index), draftId, baseAnswers)
            .mustBe(CountryOfResidenceYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence yes no page -> No" when {

          "NINO answered" must {
            "-> Mental capacity yes no page" in {
              val answers = baseAnswers
                .set(NationalInsuranceYesNoPage(index), true).success.value
                .set(NationalInsuranceNumberPage(index), nino).success.value
                .set(CountryOfResidenceYesNoPage(index), false).success.value

              navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
                .mustBe(MentalCapacityYesNoController.onPageLoad(index, draftId))
            }
          }

          "NINO not answered" must {
            "-> Address yes no page" in {
              val answers = baseAnswers
                .set(NationalInsuranceYesNoPage(index), false).success.value
                .set(CountryOfResidenceYesNoPage(index), false).success.value

              navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
                .mustBe(AddressYesNoController.onPageLoad(index, draftId))
            }
          }
        }

        "CountryOfResidence yes no page -> Yes -> CountryOfResidence Uk yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
            .mustBe(CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidenceInUK yes no page -> No -> CountryOfResidence page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
            .mustBe(CountryOfResidenceController.onPageLoad(index, draftId))
        }

        "CountryOfResidenceInUK yes no page -> Yes" when {
          "NINO answered" must {
            "-> Mental capacity yes no page" in {
              val answers = baseAnswers
                .set(NationalInsuranceYesNoPage(index), true).success.value
                .set(NationalInsuranceNumberPage(index), nino).success.value
                .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value

              navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
                .mustBe(MentalCapacityYesNoController.onPageLoad(index, draftId))
            }
          }

          "NINO not answered" must {
            "-> Address yes no page" in {
              val answers = baseAnswers
                .set(NationalInsuranceYesNoPage(index), false).success.value
                .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value

              navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
                .mustBe(AddressYesNoController.onPageLoad(index, draftId))
            }
          }
        }

        "Country of residence" when {
          "NINO answered" must {
            "-> Mental capacity yes no page" in {
              val answers = baseAnswers
                .set(NationalInsuranceYesNoPage(index), true).success.value
                .set(NationalInsuranceNumberPage(index), nino).success.value
                .set(CountryOfResidencePage(index), ES).success.value

              navigator.nextPage(CountryOfResidencePage(index), draftId, answers)
                .mustBe(MentalCapacityYesNoController.onPageLoad(index, draftId))
            }
          }

          "NINO not answered" must {
            "-> Address yes no page" in {
              val answers = baseAnswers
                .set(NationalInsuranceYesNoPage(index), false).success.value
                .set(CountryOfResidencePage(index), ES).success.value

              navigator.nextPage(CountryOfResidencePage(index), draftId, answers)
                .mustBe(AddressYesNoController.onPageLoad(index, draftId))
            }
          }
        }

        "AddressYesNoPage -> Yes -> AddressUkYesNoPage" in {
          val answers = baseAnswers
            .set(AddressYesNoPage(index), true).success.value

          navigator.nextPage(AddressYesNoPage(index), draftId, answers)
            .mustBe(AddressUkYesNoController.onPageLoad(index, draftId))
        }

        "Address Yes No page -> No -> MentalCapacityYesNo page" in {
          val answers = baseAnswers
            .set(AddressYesNoPage(index), false).success.value

          navigator.nextPage(AddressYesNoPage(index), draftId, answers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, draftId))
        }

        "AddressUkYesNoPage -> Yes -> UKAddressPage" in {
          val answers = baseAnswers
            .set(AddressUkYesNoPage(index), true).success.value

          navigator.nextPage(AddressUkYesNoPage(index), draftId, answers)
            .mustBe(UkAddressController.onPageLoad(index, draftId))
        }

        "AddressUkYesNoPage -> No -> NonUKAddressPage" in {
          val answers = baseAnswers
            .set(AddressUkYesNoPage(index), false).success.value

          navigator.nextPage(AddressUkYesNoPage(index), draftId, answers)
            .mustBe(NonUkAddressController.onPageLoad(index, draftId))
        }

        "UKAddressPage -> PassportDetailsYesNoController" in {
          navigator.nextPage(UkAddressPage(index), draftId, baseAnswers)
            .mustBe(PassportDetailsYesNoController.onPageLoad(index, draftId))
        }

        "NonUKAddressPage -> PassportDetailsYesNoController" in {
          navigator.nextPage(NonUkAddressPage(index), draftId, baseAnswers)
            .mustBe(PassportDetailsYesNoController.onPageLoad(index, draftId))
        }

        "PassportDetailsYesNoPage -> Yes -> PassportDetailsPage" in {
          val answers = baseAnswers
            .set(PassportDetailsYesNoPage(index), true).success.value

          navigator.nextPage(PassportDetailsYesNoPage(index), draftId, answers)
            .mustBe(PassportDetailsController.onPageLoad(index, draftId))
        }

        "PassportDetailsYesNoPage -> No -> IDCardDetailsYesNoPage" in {
          val answers = baseAnswers
            .set(PassportDetailsYesNoPage(index), false).success.value

          navigator.nextPage(PassportDetailsYesNoPage(index), draftId, answers)
            .mustBe(IDCardDetailsYesNoController.onPageLoad(index, draftId))
        }

        "PassportDetailsPage -> MentalCapacityYesNo page" in {
          navigator.nextPage(PassportDetailsPage(index), fakeDraftId, baseAnswers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
        }

        "IDCardDetailsYesNoPage -> Yes -> IDCardDetailsPage" in {
          val answers = baseAnswers
            .set(IDCardDetailsYesNoPage(index), true).success.value

          navigator.nextPage(IDCardDetailsYesNoPage(index), draftId, answers)
            .mustBe(IDCardDetailsController.onPageLoad(index, draftId))
        }

        "IDCardDetails Yes No page -> No -> MentalCapacityYesNo page" in {
          val answers = baseAnswers
            .set(IDCardDetailsYesNoPage(index), false).success.value

          navigator.nextPage(IDCardDetailsYesNoPage(index), draftId, answers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, draftId))
        }

        "IDCardDetails page -> MentalCapacityYesNo page" in {
          navigator.nextPage(IDCardDetailsPage(index), fakeDraftId, baseAnswers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
        }

        "Mental Capacity Yes No page -> Check details page" in {
          navigator.nextPage(MentalCapacityYesNoPage(index), draftId, baseAnswers)
            .mustBe(CheckDetailsController.onPageLoad(index, draftId))
        }
      }

      "non-taxable" when {

        val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = false)

        "NamePage -> DateOfBirthYesNoPage" in {
          navigator.nextPage(NamePage(index), draftId, baseAnswers)
            .mustBe(DateOfBirthYesNoController.onPageLoad(index, draftId))
        }

        "DateOfBirthYesNoPage -> Yes -> DateOfBirthPage" in {
          val answers = baseAnswers.set(DateOfBirthYesNoPage(index), true).success.value

          navigator.nextPage(DateOfBirthYesNoPage(index), draftId, answers)
            .mustBe(DateOfBirthController.onPageLoad(index, draftId))
        }

        "Date of birth yes no page -> No -> CountryOfNationality Yes No page" in {
          val answers = baseAnswers
            .set(DateOfBirthYesNoPage(index), false).success.value

          navigator.nextPage(DateOfBirthYesNoPage(index), draftId, answers)
            .mustBe(CountryOfNationalityYesNoController.onPageLoad(index, draftId))
        }

        "Date of birth page -> CountryOfNationality Yes No page" in {
          navigator.nextPage(DateOfBirthPage(index), draftId, baseAnswers)
            .mustBe(CountryOfNationalityYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfNationality yes no page -> No -> Country of residency yes no page" in {
          val answers = baseAnswers
            .set(CountryOfNationalityYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfNationalityYesNoPage(index), draftId, answers)
            .mustBe(CountryOfResidenceYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfNationality yes no page -> Yes -> CountryOfNationality Uk yes no page" in {
          val answers = baseAnswers
            .set(CountryOfNationalityYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfNationalityYesNoPage(index), draftId, answers)
            .mustBe(CountryOfNationalityInTheUkYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfNationalityInUK yes no page -> No -> CountryOfNationality page" in {
          val answers = baseAnswers
            .set(CountryOfNationalityInTheUkYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfNationalityInTheUkYesNoPage(index), draftId, answers)
            .mustBe(CountryOfNationalityController.onPageLoad(index, draftId))
        }

        "CountryOfNationalityInUK yes no page -> Yes -> Country of residency yes no page" in {
          val answers = baseAnswers
            .set(CountryOfNationalityInTheUkYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfNationalityInTheUkYesNoPage(index), draftId, answers)
            .mustBe(CountryOfResidenceYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfNationality -> Country of residency Yes No page" in {
          val answers = baseAnswers
            .set(CountryOfNationalityPage(index), ES).success.value

          navigator.nextPage(CountryOfNationalityPage(index), draftId, answers)
            .mustBe(CountryOfResidenceYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence yes no page -> No -> Mental capacity yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence yes no page -> Yes -> CountryOfResidence Uk yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
            .mustBe(CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidenceInUK yes no page -> No -> CountryOfResidence page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
            .mustBe(CountryOfResidenceController.onPageLoad(index, draftId))
        }

        "CountryOfResidenceInUK yes no page -> Yes -> Mental capacity yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence (with Nino) -> MentalCapacityYesNo page" in {
          val answers = baseAnswers
            .set(NationalInsuranceYesNoPage(index), true).success.value
            .set(CountryOfResidencePage(index), ES).success.value

          navigator.nextPage(CountryOfResidencePage(index), draftId, answers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence (with No Nino) -> Mental capacity Yes No page" in {
          val answers = baseAnswers
            .set(NationalInsuranceYesNoPage(index), false).success.value
            .set(CountryOfResidencePage(index), ES).success.value

          navigator.nextPage(CountryOfResidencePage(index), draftId, answers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, draftId))
        }

        "Mental Capacity Yes No page -> Check details page" in {
          navigator.nextPage(MentalCapacityYesNoPage(index), draftId, baseAnswers)
            .mustBe(CheckDetailsController.onPageLoad(index, draftId))
        }
      }
    }
  }
}
