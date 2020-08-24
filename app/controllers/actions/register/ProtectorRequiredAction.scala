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
import viewmodels.addAnother.ProtectorViewModel

import scala.concurrent.{ExecutionContext, Future}

class ProtectorRequiredAction(page: QuestionPage[ProtectorViewModel], draftId: String)(implicit val executionContext: ExecutionContext)
  extends ActionRefiner[RegistrationDataRequest, ProtectorRequiredRequest] {

  override protected def refine[A](request: RegistrationDataRequest[A]): Future[Either[Result, ProtectorRequiredRequest[A]]] = {
    Future.successful(
      request.userAnswers.get(page) match {
        case Some(protector) =>
          Right(register.ProtectorRequiredRequest(request, protector))
        case _ =>
          Logger.info(s"[ProtectorRequiredAction] Did not find protector")
          Left(Redirect(controllers.register.routes.AddAProtectorController.onPageLoad(draftId)))
      }
    )
  }
}

class ProtectorRequiredActionImpl @Inject()(implicit val executionContext: ExecutionContext) {
  def apply(page: QuestionPage[ProtectorViewModel], draftId: String): ProtectorRequiredAction = new ProtectorRequiredAction(page, draftId)
}
