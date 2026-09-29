/*
 * Copyright 2026 HM Revenue & Customs
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

package handlers

import base.SpecBase
import views.html.{ErrorTemplate, PageNotFoundView}

class ErrorHandlerSpec extends SpecBase {

  private lazy val handler = injector.instanceOf[ErrorHandler]

  "ErrorHandler" must {

    "render the standard error template" in {
      val result = handler.standardErrorTemplate("title", "heading", "message")(using fakeRequest).futureValue

      result.toString mustEqual
        injector.instanceOf[ErrorTemplate].apply("title", "heading", "message")(using fakeRequest, messages).toString
    }

    "render the not found template" in {
      val result = handler.notFoundTemplate(using fakeRequest).futureValue

      result.toString mustEqual
        injector.instanceOf[PageNotFoundView].apply()(using fakeRequest, messages).toString
    }
  }
}