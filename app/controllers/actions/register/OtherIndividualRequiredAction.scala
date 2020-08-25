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

package controllers.actions.register

import controllers.Assets.Redirect
import controllers.actions.register
import javax.inject.Inject
import models.requests.RegistrationDataRequest
import pages.QuestionPage
import play.api.Logger
import play.api.mvc.{ActionRefiner, Result}
import viewmodels.addAnother.OtherIndividualViewModel

import scala.concurrent.{ExecutionContext, Future}

class OtherIndividualRequiredAction(page: QuestionPage[OtherIndividualViewModel], draftId: String)(implicit val executionContext: ExecutionContext)
  extends ActionRefiner[RegistrationDataRequest, OtherIndividualRequiredRequest] {

  override protected def refine[A](request: RegistrationDataRequest[A]): Future[Either[Result, OtherIndividualRequiredRequest[A]]] = {
    Future.successful(
      request.userAnswers.get(page) match {
        case Some(otherIndividual) =>
          Right(register.OtherIndividualRequiredRequest(request, otherIndividual))
        case _ =>
          Logger.info(s"[OtherIndividualRequiredAction] Did not find otherIndividual")
          Left(Redirect(controllers.register.routes.AddOtherIndividualController.onPageLoad(draftId)))
      }
    )
  }
}

class OtherIndividualRequiredActionImpl @Inject()(implicit val executionContext: ExecutionContext) {
  def apply(page: QuestionPage[OtherIndividualViewModel], draftId: String): OtherIndividualRequiredAction = new OtherIndividualRequiredAction(page, draftId)
}
