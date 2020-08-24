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

package controllers.actions.register.individual

import controllers.actions.ProtectorNameRequest
import javax.inject.Inject
import models.requests.RegistrationDataRequest
import pages.register.individual.NamePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.ActionTransformer

import scala.concurrent.{ExecutionContext, Future}

class NameRequiredActionAction(index: Int)(implicit val executionContext: ExecutionContext, val messagesApi: MessagesApi)
  extends ActionTransformer[RegistrationDataRequest, ProtectorNameRequest] with I18nSupport {

  override protected def transform[A](request: RegistrationDataRequest[A]): Future[ProtectorNameRequest[A]] = {
    Future.successful(ProtectorNameRequest[A](request,
      getName(request)
    ))
  }

  private def getName[A](request: RegistrationDataRequest[A]): String = {
    request.userAnswers.get(NamePage(index)) match {
      case Some(name) => name.toString
      case _ => request.messages(messagesApi)("protector.name.default")
    }
  }
}

class NameRequiredAction @Inject()()
                                  (implicit val executionContext: ExecutionContext, val messagesApi: MessagesApi) {
  def apply(index: Int): NameRequiredActionAction = new NameRequiredActionAction(index)
}
