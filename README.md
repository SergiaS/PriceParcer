## Описание для реализации (ТЗ)
Парс цен разных сайтов - акцент на велотематику. Поиск строкового запроса в телеграмм-боте (напр., `Giro ISODE MIPS`).

Необходимо реализовать:
  * возможность поиска по всем сайтам - реализовать многопоточность (для парса каждого сайта);
    * минус - ответ будет большим:
      * для смартфона не удобно;
      * для ПК норм, возможно стоит сделать веб-версию.
  * возможность поиска по конкретному сайту из списка;
  * возможность добавления запроса в избранное и отслеживание изменения цены;
  * возможность удаления запроса из избранного;
  * сохранение состояния для разных пользователей;
  * возможность выбора/смены языка (ua/ru/en) - internationalization i18;
  * может быть выбор отображаемой валюты;
  * рандомные ответы одной тематики с приколами;

## Plans (selected - is done):
- `wiggle.co.uk`
- chainreactioncycles.com
- bike-components.de
- bike-discount.de
- `bike24.com`
  - but in future need to rebuild - parse status with Germany country, and parse price with chosen country to delivery
  - maybe also will be interesting to comparing `DE_price + delivery + NP` and `UA_price + delivery`
- ebay (com, de, co.uk, pl)

and many others ...


# What can do this console parser for now:
## SHOPS:
For now all shops can save data in 2 different templates - `color and size` or just `group`. 
When user have selected all parsed options, then will be added the `status` and `price` of the parsed item.

### 1 - Wiggle
- Gets first 48 parsedItems and prices variations for each parsedItem in one searched request.
  **It parse only first 48 wiggle parsedItems of the request.**

- By default, the result will be sort by priority - first it's `price` and second - `color`.
  Option `size` is not included because there are could be different value names like "M" and "Medium" in one product.
  Also added `stock status` value for all options.

- Response will return first 48 wiggle parsedItems which will be founded by the request - it isn't search by one page.
  - `?o=2` - site sorting - second parameter (Price: Low to High).
  - `&ris=1` - site sorting filter - show only parsedItems in stock.
  - `&prevDestCountryId=99&dest=1` - UK locale. For Ukraine Garmin products isn't available, but for UK is ok...
  - `&curr=USD` - you can set up currency by editing it to the url (USD, EUR, GBP...).
  

