/*
 * Copyright 2024 HM Revenue & Customs
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

package mapping.register

import models.{Address, AddressType, InternationalAddress, PassportOrIdCardDetails, PassportType, UkAddress}
import utils.Constants.GB

object IdentificationMapper  {

  def buildAddress(address: Address): Option[AddressType] = {
    address match {
      case a: UkAddress => Some(buildUkAddress(a))
      case a: InternationalAddress => Some(buildInternationalAddress(a))
    }
  }

  private def buildUkAddress(address: UkAddress): AddressType = {
    AddressType(
      line1 = address.line1,
      line2 = address.line2,
      line3 = address.line3,
      line4 = address.line4,
      postCode = Some(address.postcode),
      country = GB
    )
  }

  private def buildInternationalAddress(address: InternationalAddress): AddressType = {
    AddressType(
      line1 = address.line1,
      line2 = address.line2,
      line3 = address.line3,
      line4 = None,
      postCode = None,
      country = address.country
    )
  }

  def buildPassport(details: PassportOrIdCardDetails): Option[PassportType] = {
    Some(PassportType(details.cardNumber, details.expiryDate, details.country))
  }

  def buildValue[A, B](o1: Option[A], o2: Option[A])
                      (build: A => Option[B]): Option[B] = (o1, o2) match {
    case (Some(v), _) => build(v)
    case (_, Some(v)) => build(v)
    case _ => None
  }

}
