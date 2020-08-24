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

import config.FrontendAppConfig
import javax.inject.Inject
import models.ReadableUserAnswers
import pages.Page
import pages.register.{AddAProtectorPage, AddAProtectorYesNoPage, AnswersPage, IndividualOrBusinessPage, TrustHasProtectorYesNoPage}
import play.api.mvc.Call
import controllers.register.{routes => rts}
import controllers.register.business.{routes => brts}
import controllers.register.individual.{routes => irts}
import models.register.pages.AddAProtector
import models.register.pages.IndividualOrBusinessToAdd.{Business, Individual}
import sections.{BusinessProtectors, IndividualProtectors}

class ProtectorNavigator @Inject()(config: FrontendAppConfig) extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call = route(draftId, config)(page)(userAnswers)

  private def route(draftId: String, config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case AnswersPage => _ => rts.AddAProtectorController.onPageLoad(draftId)
    case AddAProtectorPage => addProtectorRoute(draftId, config)
    case AddAProtectorYesNoPage => addAProtectorYesNoRoute(draftId, config)
    case IndividualOrBusinessPage => individualOrBusinessRoute(draftId)
    case TrustHasProtectorYesNoPage => trustHasProtectorRoute(draftId)
  }

  private def protectorsCompletedRoute(draftId: String, config: FrontendAppConfig): Call = {
    Call("GET", config.registrationProgressUrl(draftId))
  }

  private def trustHasProtectorRoute(draftId: String)(userAnswers: ReadableUserAnswers) : Call =
    userAnswers.get(TrustHasProtectorYesNoPage) match {
      case Some(true) => controllers.register.routes.InfoController.onPageLoad(draftId)
      case Some(false) => protectorsCompletedRoute(draftId, config)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def individualOrBusinessRoute(draftId: String)(userAnswers: ReadableUserAnswers) : Call =
    userAnswers.get(IndividualOrBusinessPage) match {
      case Some(Individual) => routeToIndividualProtectorIndex(userAnswers, draftId)
      case Some(Business) => routeToBusinessProtectorIndex(userAnswers, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def routeToIndividualProtectorIndex(userAnswers: ReadableUserAnswers, draftId: String): Call = {
    val individualProtectors = userAnswers.get(IndividualProtectors).getOrElse(List.empty)
    irts.NameController.onPageLoad(individualProtectors.size, draftId)
  }

  private def routeToBusinessProtectorIndex(userAnswers: ReadableUserAnswers, draftId: String): Call = {
    val businessProtectors = userAnswers.get(BusinessProtectors).getOrElse(List.empty)
    brts.NameController.onPageLoad(businessProtectors.size, draftId)
  }

  private def addProtectorRoute(draftId: String, config: FrontendAppConfig)(answers: ReadableUserAnswers): Call = {
    val addAnother = answers.get(AddAProtectorPage)
    addAnother match {
      case Some(AddAProtector.YesNow) =>
        controllers.register.routes.IndividualOrBusinessController.onPageLoad(draftId)
      case Some(AddAProtector.YesLater) => protectorsCompletedRoute(draftId, config)
      case Some(AddAProtector.NoComplete) => protectorsCompletedRoute(draftId, config)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def addAProtectorYesNoRoute(draftId: String, config: FrontendAppConfig)(answers: ReadableUserAnswers): Call = {
    val add = answers.get(AddAProtectorYesNoPage)

    add match {
      case Some(true) =>
        controllers.register.routes.IndividualOrBusinessController.onPageLoad(draftId)
      case Some(false) => protectorsCompletedRoute(draftId, config)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

}

