/*
 * Copyright 2020 HM Revenue & Customs
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
import generators.Generators
import models.Status.InProgress
import models.UserAnswers
import models.register.pages.{AddAProtector, IndividualOrBusinessToAdd}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register._
import pages.register.business.NamePage
import play.api.mvc.Call
import controllers.register.{routes => rts}
import controllers.register.business.{routes => brts}
import controllers.register.individual.{routes => irts}

class ProtectorNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private def protectorsCompletedRoute(draftId: String, config: FrontendAppConfig): Call = {
    Call("GET", config.registrationProgressUrl(draftId))
  }

  val navigator: ProtectorNavigator = injector.instanceOf[ProtectorNavigator]

   "AnswersPage" when {
     "go to AddAProtectorPage from AnswersPage" in {
       forAll(arbitrary[UserAnswers]) {
         userAnswers =>
           navigator.nextPage(AnswersPage, fakeDraftId, userAnswers)
             .mustBe(controllers.register.routes.AddAProtectorController.onPageLoad(fakeDraftId))
       }
     }
   }


  "AddAProtectorYesNoPage" when {

    "go to IndividualOrBusinessPage from AddAProtectorYesNoPage when selected yes" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(AddAProtectorYesNoPage, true).success.value

          navigator.nextPage(AddAProtectorYesNoPage, fakeDraftId, answers)
            .mustBe(controllers.register.routes.IndividualOrBusinessController.onPageLoad(fakeDraftId))
      }

    }

    "go to RegistrationProgress from AddAProtectorYesNoPage when selected no" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(AddAProtectorYesNoPage, false).success.value

          navigator.nextPage(AddAProtectorYesNoPage, fakeDraftId, answers)
            .mustBe(protectorsCompletedRoute(fakeDraftId, frontendAppConfig))
      }
    }

    "go to IndividualOrBusinessPage from AddAProtectorPage when selected add them now" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(AddAProtectorPage, AddAProtector.YesNow).success.value

          navigator.nextPage(AddAProtectorPage, fakeDraftId, answers)
            .mustBe(controllers.register.routes.IndividualOrBusinessController.onPageLoad(fakeDraftId))
      }

    }
  }


  "go to RegistrationProgress from AddAProtectorPage" when {

    "selecting add them later" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers
            .set(NamePage(0), "Business").success.value
            .set(AddAProtectorPage, AddAProtector.YesLater).success.value

          navigator.nextPage(AddAProtectorPage, fakeDraftId, answers)
            .mustBe(protectorsCompletedRoute(fakeDraftId, frontendAppConfig))
      }
    }

    "selecting added them all" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers
            .set(NamePage(0), "Business").success.value
            .set(AddAProtectorPage, AddAProtector.NoComplete).success.value

          navigator.nextPage(AddAProtectorPage, fakeDraftId, answers)
            .mustBe(protectorsCompletedRoute(fakeDraftId, frontendAppConfig))
      }
    }

  }


  "IndividualOrBusinessPage" when {

    "go to NamePage from IndividualOrBusinessPage when Business option selected" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualOrBusinessPage, value = IndividualOrBusinessToAdd.Business).success.value

          navigator.nextPage(IndividualOrBusinessPage, fakeDraftId, answers)
            .mustBe(brts.NameController.onPageLoad(0, fakeDraftId))
      }
    }

    "go to NamePage from IndividualOrBusinessPage when Individual option selected" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(IndividualOrBusinessPage, value = IndividualOrBusinessToAdd.Individual).success.value

          navigator.nextPage(IndividualOrBusinessPage, fakeDraftId, answers)
            .mustBe(irts.NameController.onPageLoad(0, fakeDraftId))
      }
    }
  }

  "TrustHasProtectorYesNoPage" when {

    "go to InfoPage from TrustHasProtectorYesNoPage when yes selected" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(TrustHasProtectorYesNoPage, value = true).success.value

          navigator.nextPage(TrustHasProtectorYesNoPage, fakeDraftId, answers)
            .mustBe(rts.InfoController.onPageLoad(fakeDraftId))
      }
    }

    "go to RegistrationProgress from TrustHasProtectorYesNoPage when no selected" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(TrustHasProtectorYesNoPage, value = false).success.value

          navigator.nextPage(TrustHasProtectorYesNoPage, fakeDraftId, answers)
            .mustBe(protectorsCompletedRoute(fakeDraftId, frontendAppConfig))
      }
    }

  }

}
